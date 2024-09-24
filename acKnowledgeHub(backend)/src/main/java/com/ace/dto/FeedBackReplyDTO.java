package com.ace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedBackReplyDTO {
    private Integer id;
    private String content;
    private String reply;
    private LocalDateTime createdAt;
    private String replyBy;
    private String staffName;
    private LocalDateTime replyAt;
    private Integer announcementId;
    private String photoPath;
    private String replyPhotoPath;
}
