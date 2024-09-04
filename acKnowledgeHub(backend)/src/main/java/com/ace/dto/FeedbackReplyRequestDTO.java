package com.ace.dto;

import lombok.Data;

@Data
public class FeedbackReplyRequestDTO {
    private Integer feedbackId;
    private String replyText;
    private Integer replyBy;
}
