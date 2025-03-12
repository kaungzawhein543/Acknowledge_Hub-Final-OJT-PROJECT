package com.ace.dto;

import lombok.Data;

import java.util.Date;

@Data
public class FeedbackListResponseDTO {
    private Integer id;
    private String content;
    private String staffName;
    private String reply;
    private String replyBy;
    private Date createdAt;
    private Date replyAt;
    private String photoPath;
    private String replyPhotoPath;

    public FeedbackListResponseDTO(Integer id, String content, String staffName, String reply, String replyBy, Date createdAt, Date replyAt,String photoPath,String replyPhotoPath) {
        this.id = id;
        this.content = content;
        this.staffName = staffName;
        this.reply = reply;
        this.replyBy = replyBy;
        this.createdAt = createdAt;
        this.replyAt = replyAt;
        this.photoPath = photoPath;
        this.replyPhotoPath = replyPhotoPath;
    }
}
