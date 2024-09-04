package com.ace.dto;

import lombok.Data;

import java.util.Date;

@Data
public class FeedbackListResponseDTO {
    private Integer feedbackId;
    private String content;
    private String staffName;
    private String reply;
    private String replyBy;
    private Date createdAt;
    private Date replyAt;

    public FeedbackListResponseDTO(Integer feedbackId, String content, String staffName, String reply, String replyBy, Date createdAt, Date replyAt) {
        this.feedbackId = feedbackId;
        this.content = content;
        this.staffName = staffName;
        this.reply = reply;
        this.replyBy = replyBy;
        this.createdAt = createdAt;
        this.replyAt = replyAt;
    }
}
