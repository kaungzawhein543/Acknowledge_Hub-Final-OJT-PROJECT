package com.ace.configuration;

import com.ace.bot.MyTelegramBot;
import com.ace.service.AnnouncementService;
import com.ace.service.StaffService;
import com.ace.service.UserNotedAnnouncementService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
@Configuration
public class BotConfig {

    @Value("${bot.token}")
    private String botToken;

    @Value("${bot.name}")
    private String botUsername;

    private final StaffService staffService;
    private final UserNotedAnnouncementService userNotedAnnouncementService;
    private final AnnouncementService announcementService;

    public BotConfig(StaffService staffService, UserNotedAnnouncementService userNotedAnnouncementService, AnnouncementService announcementService) {
        this.staffService = staffService;
        this.userNotedAnnouncementService = userNotedAnnouncementService;
        this.announcementService = announcementService;
    }

    @Bean
    public MyTelegramBot telegramBot() {
        DefaultBotOptions options = new DefaultBotOptions();
        return new MyTelegramBot(options, botToken, botUsername,staffService,userNotedAnnouncementService,announcementService);
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(MyTelegramBot telegramBot) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(telegramBot);
        return telegramBotsApi;
    }

    @Bean
    public DefaultBotOptions defaultBotOptions() {
        return new DefaultBotOptions();
    }
}
