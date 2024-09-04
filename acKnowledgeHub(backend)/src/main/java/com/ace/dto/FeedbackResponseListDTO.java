package com.ace.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FeedbackResponseListDTO {
    private Integer id;
    private String content;
    private LocalDateTime create_at;
    private String staffName;
    private String announcementTitle;
    private String staffCompany;
    private String staffDepartment;
    private String staffPosition;

    public FeedbackResponseListDTO(Integer id, String content, LocalDateTime create_at, String staffName, String announcementTitle, String staffCompany, String staffDepartment, String staffPosition) {
        this.id = id;
        this.content = content;
        this.create_at = create_at;
        this.staffName = staffName;
        this.announcementTitle = announcementTitle;
        this.staffCompany = staffCompany;
        this.staffDepartment = staffDepartment;
        this.staffPosition = staffPosition;
    }
}
