package com.ace.controller;

import com.ace.dto.NotificationDTO;
import com.ace.entity.Notification;
import com.ace.exceptions.ErrorResponse;
import com.ace.security.JwtUtil;
import com.ace.service.NotificationService;
import com.ace.service.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class NotificationController {
    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final SimpMessagingTemplate simpMessagingTemplate;


    @Value("${jwt.secret}")
    private String jwtSecret;

    public NotificationController(NotificationService notificationService, JwtUtil jwtUtil, TokenBlacklistService tokenBlacklistService, SimpMessagingTemplate simpMessagingTemplate) {
        this.notificationService = notificationService;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }
   @PutMapping("/notifications/status")
    public ResponseEntity<?> updateNotificationStatus(
            @RequestBody List<Integer> notificationIds,
            @RequestHeader("X-Staff-Id") int staffId) {

        System.out.println("Request body: " + notificationIds);

        if (notificationIds == null || notificationIds.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        System.out.println("Received Staff ID: " + staffId);

        // Update notifications status
        notificationService.markNotificationsAsInactive(notificationIds);


        simpMessagingTemplate.convertAndSend("/topic/notificationStatusUpdate", notificationIds);

        return ResponseEntity.ok().build();
    }

    @MessageMapping("/sendNotification")
    @SendTo("/topic/notifications")
    public NotificationDTO sendNotification(NotificationDTO notification) {
        System.out.println("Sending NotificationDTO: " + notification);
     return notification;
    }

    @MessageMapping("/getNotifications")
    @SendTo("/topic/notifications")
    public List<NotificationDTO> getNotifications(@Payload int staffId) {
        List<NotificationDTO> notifications = notificationService.getNotificationsByStaffId(staffId);
        System.out.println("Sending notifications: " + notifications);
        return notifications;
    }

}
