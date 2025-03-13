package com.ace.dto;

import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class AnnouncementListDTO {
    private Integer id;
    private String title;
    private String description;
    private String createStaff;
    private String category;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime scheduleAt;
    private byte groupStatus;
    private String file;

    public AnnouncementListDTO(Integer id, String title, String description, String createStaff, String category, String status, LocalDateTime createdAt, LocalDateTime scheduleAt, byte groupStatus, String file) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createStaff = createStaff;
        this.category = category;
        this.status = status;
        this.createdAt = createdAt;
        this.scheduleAt = scheduleAt;
        this.groupStatus = groupStatus;
        this.file = file;
    }
}
