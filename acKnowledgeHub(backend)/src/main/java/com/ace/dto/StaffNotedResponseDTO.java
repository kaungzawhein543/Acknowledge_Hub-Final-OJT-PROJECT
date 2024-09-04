package com.ace.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
@Data
public class StaffNotedResponseDTO {
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private Timestamp notedAt;
    private String file;

    public StaffNotedResponseDTO(String title, String description, LocalDateTime createdAt, Timestamp notedAt, String file) {
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.notedAt = notedAt;
        this.file = file;
    }
}
