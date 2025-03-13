package com.ace.announcements.feedbackAndReply.dtos;

import lombok.Data;

@Data
public class FeedbackResponseDTO {
    private Integer id;
    private Integer staffId;
    private Integer announcementId;
    private String content;
}
