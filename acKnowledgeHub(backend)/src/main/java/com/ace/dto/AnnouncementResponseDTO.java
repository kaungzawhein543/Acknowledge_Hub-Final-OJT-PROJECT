package com.ace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AnnouncementResponseDTO {
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private String createStaff;
    private String file;

}
