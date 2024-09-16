package com.ace.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class RequestAnnouncementResponseDTO {
    private Integer id;
    private String title;
    private String description;
    private Date createdAt;
    private LocalDateTime scheduleAt;
    private String category;
    private String createStaff;
    private String staffCompany;

    public RequestAnnouncementResponseDTO(Integer id, String title, String description, Date createdAt, LocalDateTime scheduleAt, String category, String createStaff, String staffCompany) {
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
