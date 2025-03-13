package com.ace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
public class AnnouncementStaffCountDTO {
    private Integer announcementId;
    private String title;
    private LocalDateTime createdAt;
    private Long staffCount;
}
