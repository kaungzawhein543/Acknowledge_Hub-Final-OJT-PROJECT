package com.ace.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnnouncementResponseListDTO {
    private Integer id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private String createStaff;
    private String category;

    public AnnouncementResponseListDTO(Integer id, String title, String description, LocalDateTime createdAt, String createStaff, String category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.createStaff = createStaff;
        this.category = category;
    }
}
