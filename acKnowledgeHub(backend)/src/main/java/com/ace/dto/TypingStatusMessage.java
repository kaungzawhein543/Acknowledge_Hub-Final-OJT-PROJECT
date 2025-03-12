package com.ace.dto;

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
