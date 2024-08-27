package com.ace.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class TelegramDTO {
    private String chatId;
    private MultipartFile text;
}
