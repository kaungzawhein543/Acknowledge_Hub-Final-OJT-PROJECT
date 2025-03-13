package com.ace.announcements.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestAnnouncementResponseDTO {
    private Integer id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime scheduleAt;
    private String category;
    private String createStaff;
    private String staffCompany;

    public RequestAnnouncementResponseDTO(Integer id, String title, String description, LocalDateTime createdAt, LocalDateTime scheduleAt, String category, String createStaff, String staffCompany) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.scheduleAt = scheduleAt;
        this.category = category;
        this.createStaff = createStaff;
        this.staffCompany = staffCompany;
    }
}
