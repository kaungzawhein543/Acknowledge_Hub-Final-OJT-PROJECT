package com.ace.service;

import com.ace.bot.MyTelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BotService {


    private final MyTelegramBot myTelegramBot;
    public BotService(MyTelegramBot myTelegramBot) {
        this.myTelegramBot = myTelegramBot;
    }

    @Async("taskExecutor")
    public void sendFile(String chatId,MultipartFile file,Integer announcementId,byte updateStatus) {
        myTelegramBot.sendPdf(chatId, file,announcementId,updateStatus);
    }
}
