package com.ace.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmailResponseDTO {
    private String email;
    private LocalDateTime expiryTime;
}
