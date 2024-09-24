package com.ace.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class FeedbackResponseListDTO {
    private Integer id;
    private String content;
    private LocalDateTime create_at;
    private String staffName;
    private String announcementTitle;
    private Integer announcementId;
    private String staffCompany;
    private String staffDepartment;
    private String staffPosition;
    private String photoPath;


    public FeedbackResponseListDTO(Integer id, String content, LocalDateTime create_at, String staffName, String announcementTitle,Integer announcementId, String staffCompany, String staffDepartment, String staffPosition,String photoPath) {
        this.id = id;
        this.content = content;
        this.create_at = create_at;
        this.staffName = staffName;
        this.announcementTitle = announcementTitle;
        this.announcementId = announcementId;
        this.staffCompany = staffCompany;
        this.staffDepartment = staffDepartment;
        this.staffPosition = staffPosition;
        this.photoPath = photoPath;
    }
}
