package com.ace.dto;

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
    private Date created_at;
    private LocalDateTime scheduleAt;
    private byte groupStatus;

    public AnnouncementListDTO(Integer id, String title, String description, String createStaff, String category, String status, Date created_at, LocalDateTime scheduleAt, byte groupStatus) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createStaff = createStaff;
        this.category = category;
        this.status = status;
        this.created_at = created_at;
        this.scheduleAt = scheduleAt;
        this.groupStatus = groupStatus;
    }

    public AnnouncementListDTO(Integer id, String title, Date created_at) {
        this.id = id;
        this.title = title;
        this.created_at = created_at;
    }
}
