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

        if (notificationIds == null || notificationIds.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Update notifications status
        notificationService.markNotificationsAsInactive(notificationIds);


        simpMessagingTemplate.convertAndSend("/topic/notificationStatusUpdate", notificationIds);

        return ResponseEntity.ok().build();
    }

    @GetMapping("api/v1/notifications/check/{staffId}/{notificationId}")
    public ResponseEntity<List<NotificationDTO>> updateNotification(@PathVariable Integer staffId,@PathVariable Integer notificationId){
        return ResponseEntity.ok(notificationService.updateNotificationCheck(notificationId,staffId));
    }

    @MessageMapping("/sendNotification")
    public void sendNotification(@Payload NotificationDTO notification) {
        try {
            simpMessagingTemplate.convertAndSend("/topic/notification/" + notification.getStaffId(), notification);

        } catch (Exception e) {

            System.err.println("Error sending notification: " + e.getMessage());
        }
    }

    @MessageMapping("/getNotifications")
    public void getNotifications(@Payload int staffId) {
        try {
            List<NotificationDTO> notifications = notificationService.getNotificationsByStaffId(staffId);
            simpMessagingTemplate.convertAndSend("/topic/notifications/" + staffId, notifications);
        } catch (Exception e) {
            System.err.println("Error fetching notifications: " + e.getMessage());
        }
    }

}
