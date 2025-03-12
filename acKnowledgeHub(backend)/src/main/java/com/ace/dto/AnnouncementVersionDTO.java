package com.ace.dto;

import lombok.Data;

@Data
public class AnnouncementVersionDTO {
    private Integer id;
    private String title;

    public AnnouncementVersionDTO(Integer id, String title) {
        this.id = id;
        this.title = title;
    }
}
