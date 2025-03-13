package com.ace.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class AnnouncementListForHrDTO {
    private Integer id;
    private String title;
    private String description;
    private String createStaff;
    private String category;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime scheduleAt;
    private byte groupStatus;
    private boolean isPublished;
    private String file;

    public AnnouncementListForHrDTO(Integer id, String title, String description, String createStaff, String category, String status, LocalDateTime created_at, LocalDateTime scheduleAt, byte groupStatus,boolean isPublished, String file) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createStaff = createStaff;
        this.category = category;
        this.status = status;
        this.createdAt = createdAt;
        this.scheduleAt = scheduleAt;
        this.groupStatus = groupStatus;
        this.isPublished = isPublished;
        this.file = file;
    }
}
