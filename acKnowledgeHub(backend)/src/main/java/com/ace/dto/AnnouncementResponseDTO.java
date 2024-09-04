package com.ace.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AnnouncementResponseDTO {
    private int id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private String createStaff;
    private String file;
    private String category;

    public AnnouncementResponseDTO(Integer id , String title, String description, LocalDateTime createdAt, String createStaff, String file, String category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.createStaff = createStaff;
        this.file = file;
        this.category = category;
    }
}
