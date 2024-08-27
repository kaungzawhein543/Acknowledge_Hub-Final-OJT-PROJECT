package com.ace.dto;

import lombok.Data;

@Data
public class AnnouncementListResponseDTO {
    private int id;
    private String title;
    private String createdAt;
    private String createdBy;
    private int notedCount;
    private int unNotedCount;

    public AnnouncementListResponseDTO(int id, String title, String createdBy, String createdAt, int notedCount, int unNotedCount) {
        this.id = id;
        this.title = title;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.notedCount = notedCount;
        this.unNotedCount = unNotedCount;
    }
}
