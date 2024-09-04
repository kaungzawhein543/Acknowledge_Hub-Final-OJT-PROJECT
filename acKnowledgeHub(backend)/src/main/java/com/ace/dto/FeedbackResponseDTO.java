package com.ace.dto;

import lombok.Data;

@Data
public class FeedbackResponseDTO {
    private Integer id;
    private Integer staffId;
    private Integer announcementId;
    private String content;
}
