package com.ace.controller;

import com.ace.dto.TelegramDTO;
import com.ace.service.BotService;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@EnableAsync
@RestController
@RequestMapping("/api/bot")
public class BotController {

    private final BotService botService;

    public BotController( BotService botService) {
        this.botService = botService;
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam("chatId") String chatId,@RequestParam("file") MultipartFile file,
                              @RequestParam("announcementId") Integer announcementId) {
        try {
            botService.sendFile(chatId,file,announcementId);
            return "Message sent successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to send message";
        }
    }
}
