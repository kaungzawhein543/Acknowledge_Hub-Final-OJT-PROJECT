package com.ace.thirdPartyNotifications.email.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmailResponseDTO {
    private String email;
    private LocalDateTime expiryTime;
}
