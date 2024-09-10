package com.ace.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
@Data
public class StaffNotedResponseDTO {
    private Integer id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private Timestamp notedAt;
    private String createStaff;

    public StaffNotedResponseDTO(Integer id, String title, String description, LocalDateTime createdAt, Timestamp notedAt, String createStaff) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.notedAt = notedAt;
        this.createStaff = createStaff;
    }
}
