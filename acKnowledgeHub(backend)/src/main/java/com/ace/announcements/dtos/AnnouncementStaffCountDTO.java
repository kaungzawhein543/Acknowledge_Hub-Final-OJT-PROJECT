package com.ace.announcements.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AnnouncementStaffCountDTO {
    private Integer announcementId;
    private String title;
    private LocalDateTime createdAt;
    private Long staffCount;
}
