package com.ace.announcements.feedbackAndReply.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypingStatusMessage {
    private Long staffId;
    private boolean isTyping;
    private Integer announcementId;
}
