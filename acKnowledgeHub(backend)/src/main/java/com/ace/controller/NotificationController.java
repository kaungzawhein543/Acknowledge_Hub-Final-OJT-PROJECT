package com.ace.controller;

import com.ace.dto.Notification;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {
    @MessageMapping("/sendNotification")
    @SendTo("/topic/notification")
    public Notification sendNotification(Notification notification) {
        return notification;
    }
}
