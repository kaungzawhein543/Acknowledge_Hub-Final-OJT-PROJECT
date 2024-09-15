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

//    private static final Logger logger = LoggerFactory.getLogger(MyTelegramBot.class);

    private final String botToken;
    private final String botUsername;
    private final StaffService staffService;
    private final UserNotedAnnouncementService userNotedAnnouncementService;
    private final AnnouncementService announcementService;

    @Autowired
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
                    if ("/start".equals(messageText)) {
                        startRequest(chatId);
                        Optional<Staff> user = staffService.findByChatId(chatId);
                        System.out.println(user);
                        if (!user.isPresent()) {
                            sendMessage(chatId, "Please provide your email address.");
                        }
                    } else if (isValidEmail(messageText)) {
                        Staff user= staffService.findByEmail(messageText);
                        if (user == null) {
                            sendMessage(chatId, "Please provide a valid email address that gives to company.");
                        } else {
                            sendMessage(chatId, "Thank you for providing your email address!");
                            staffService.saveChatId(chatId, messageText);
                        }
                    } else if (update.getMessage().hasText()) {
                        Optional<Staff> user = staffService.findByChatId(chatId);
                        if (user == null) {
                            sendMessage(chatId, "Please provide a valid email address.");
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
                if ("button".equals(action)) {
                    updateMessageWithDoneButton(chatId, messageId);
                    StaffNotedAnnouncement staffNotedAnnouncement = new StaffNotedAnnouncement();
                    Optional<Announcement> announcement = announcementService.getAnnouncementById(Integer.parseInt(fileId));
                    staffNotedAnnouncement.setAnnouncement(announcement.get());
                    Optional<Staff> user = staffService.findByChatId(chatId);
                    staffNotedAnnouncement.setStaff(user.get());
                    Optional<StaffNotedAnnouncement> notedAnnouncement =  userNotedAnnouncementService.checkNotedOrNot(user.get(),announcement.get());
                    if(!notedAnnouncement.isPresent()){
                        userNotedAnnouncementService.save(staffNotedAnnouncement);
                    }
                    AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                    answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
                    execute(answerCallbackQuery);
                }
            }
        } catch (TelegramApiException e) {
            log.error("Error handling update", e);
        }
    }

    public void sendPdf(String chatId, MultipartFile multipartFile,Integer announcementId) {
        try {
            // Send the PDF document
            InputFile inputFile = new InputFile(multipartFile.getInputStream(), multipartFile.getOriginalFilename());
            SendDocument document = new SendDocument();
            document.setChatId(chatId);
            document.setDocument(inputFile);
            try {
                execute(document);
            } catch (TelegramApiException e) {
                log.error("Error sending PDF file", e);
            }
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("If you receive message , you can do noted");
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

            // Create button with callback data indicating true
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText("Note");
            button.setCallbackData("button:"+announcementId);

            // Create keyboard row and set it to keyboard
            List<InlineKeyboardButton> row = Arrays.asList(button);
            List<List<InlineKeyboardButton>> keyboard = Arrays.asList(row);
            inlineKeyboardMarkup.setKeyboard(keyboard);
            // Set keyboard to message
            message.setReplyMarkup(inlineKeyboardMarkup);
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error("Error sending message", e);
            }
        } catch (IOException e) {
            log.error("Error processing MultipartFile", e);
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
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);
        editMessage.setText("You have noted");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("✅Noted");
        button.setCallbackData("button:done");
        List<InlineKeyboardButton> row = Arrays.asList(button);
        List<List<InlineKeyboardButton>> keyboard = Arrays.asList(row);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        editMessage.setReplyMarkup(inlineKeyboardMarkup);
        execute(editMessage);
    }

    private void startRequest(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Welcome to ACE(AcKnowledge Hub)");
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Send Email Request happening  error",new TelegramApiException());
        }
    }


    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }


    private void saveChatId(String chatId, String email) {
        staffService.saveChatId(chatId, email);
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
