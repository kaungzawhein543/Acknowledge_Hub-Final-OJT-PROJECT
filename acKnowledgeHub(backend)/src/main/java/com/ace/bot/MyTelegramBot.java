package com.ace.bot;

import com.ace.entity.Announcement;
import com.ace.entity.Staff;
import com.ace.entity.StaffNotedAnnouncement;
import com.ace.service.AnnouncementService;
import com.ace.service.StaffService;
import com.ace.service.UserNotedAnnouncementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class MyTelegramBot extends TelegramLongPollingBot {

    private final String botToken;
    private final String botUsername;
    private final StaffService staffService;
    private final UserNotedAnnouncementService userNotedAnnouncementService;
    private final AnnouncementService announcementService;

    public MyTelegramBot(DefaultBotOptions options, @Value("${bot.token}") String botToken, @Value("${bot.name}")  String botUsername,
                         StaffService staffService, UserNotedAnnouncementService userNotedAnnouncementService, AnnouncementService announcementService) {
        super(options);
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.staffService = staffService;
        this.userNotedAnnouncementService = userNotedAnnouncementService;
        this.announcementService = announcementService;
    }


    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                if (update.getMessage().hasText()) {
                    String messageText = update.getMessage().getText();
                    String chatId = update.getMessage().getChatId().toString();
                    String username = update.getMessage().getFrom().getUserName();
                    if ("/start".equals(messageText)) {
                        List<Staff> users = staffService.findByTelegramUserName(username);
                        if (users == null||users.isEmpty()) {
                            sendMessage(chatId, "You are not our companies' staff. So, our bot channel does not belong to you");
                        } else {
                            for (Staff user : users) {
                                user.setChatId(chatId);
                                staffService.save(user); // Save each updated user
                            }
                            sendMessage(chatId, "Welcome to ACE (AcKnowledge Hub)");
                        }
                    }
                }
            } else if (update.hasCallbackQuery()) {
                CallbackQuery callbackQuery = update.getCallbackQuery();
                String callbackData = callbackQuery.getData();
                String chatId = callbackQuery.getMessage().getChatId().toString();
                Integer messageId = callbackQuery.getMessage().getMessageId();

                String[] callbackParts = callbackData.split(":");
                String action = callbackParts[0];
                String fileId = callbackParts.length > 1 ? callbackParts[1] : null;

                // Handle button action
                if ("button".equals(action)) {
                    // Update message with noted button
                    updateMessageWithDoneButton(chatId, messageId);

                    // Handle the business logic for noting the announcement
                    StaffNotedAnnouncement staffNotedAnnouncement = new StaffNotedAnnouncement();
                    Optional<Announcement> announcement = announcementService.getAnnouncementById(Integer.parseInt(fileId));
                    staffNotedAnnouncement.setAnnouncement(announcement.get());
                    Optional<Staff> user = staffService.findByChatId(chatId);
                    staffNotedAnnouncement.setStaff(user.get());

                    Optional<StaffNotedAnnouncement> notedAnnouncement = userNotedAnnouncementService.checkNotedOrNot(user.get(), announcement.get());
                    if (!notedAnnouncement.isPresent()) {
                        userNotedAnnouncementService.save(staffNotedAnnouncement);
                    }

                    // Send the answer to the callback query (to confirm action)
                    AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                    answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
                    answerCallbackQuery.setText("Your action has been noted.");
                    answerCallbackQuery.setShowAlert(false); // Show an alert if necessary
                    execute(answerCallbackQuery);
                }
            }
        } catch (TelegramApiException e) {
            log.error("Error handling update", e);
        }
    }

    public void sendPdf(String chatId, MultipartFile multipartFile, Announcement announcement, byte updateStatus) {
        try {
            // Prepare the document
            InputFile inputFile = new InputFile(multipartFile.getInputStream(), multipartFile.getOriginalFilename());
            SendDocument document = new SendDocument();
            document.setChatId(chatId);
            document.setDocument(inputFile);

            // Set the caption to include title and description
            String caption;
            if (updateStatus > 0) {
                caption = "\nHere is an updated version for you\n\n*" + announcement.getTitle() + "*\n\n"+ announcement.getDescription();
            } else {
                caption = "\n*" + announcement.getTitle() + "*\n\n" + announcement.getDescription();
            }
            document.setCaption(caption);
            document.setParseMode("Markdown");  // Enables Markdown formatting in the caption

            // Add inline keyboard (button) directly to the document message
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText("Note");
            button.setCallbackData("button:" + announcement.getId());
            List<InlineKeyboardButton> row = Arrays.asList(button);
            List<List<InlineKeyboardButton>> keyboard = Arrays.asList(row);
            inlineKeyboardMarkup.setKeyboard(keyboard);
            document.setReplyMarkup(inlineKeyboardMarkup);

            // Send the document with caption and inline button
            execute(document);

        } catch (TelegramApiException | IOException e) {
            log.error("Error sending PDF file with caption", e);
        }
    }

    public void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Send Email Request happening  error",new TelegramApiException());
        }
    }

    private void updateMessageWithDoneButton(String chatId, Integer messageId) throws TelegramApiException {
        // Instead of changing the caption or text, we are only going to update the buttons (keyboard)
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(chatId);
        editMessageReplyMarkup.setMessageId(messageId);

        // Create a new button that shows "✅Noted"
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("✅Noted");
        button.setCallbackData("button:done"); // This callback prevents further actions

        // Update the inline keyboard with the new button
        List<InlineKeyboardButton> row = Arrays.asList(button);
        List<List<InlineKeyboardButton>> keyboard = Arrays.asList(row);
        inlineKeyboardMarkup.setKeyboard(keyboard);

        // Set the new keyboard (buttons) without changing the original message text
        editMessageReplyMarkup.setReplyMarkup(inlineKeyboardMarkup);

        // Execute the edit for the reply markup only
        execute(editMessageReplyMarkup);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
