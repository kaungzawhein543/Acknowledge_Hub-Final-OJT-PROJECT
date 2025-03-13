package com.ace.announcements.feedbackAndReply.dtos;

import lombok.Data;

@Data
public class FeedbackReplyRequestDTO {
    private Integer feedbackId;
    private String replyText;
    private Integer replyBy;
}
