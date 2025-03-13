package com.ace.announcements.feedbackAndReply.dtos;

import lombok.Data;

@Data
public class FeedbackRequestDTO {
    private Integer staffId;
    private Integer announcementId;
    private String content;

}
