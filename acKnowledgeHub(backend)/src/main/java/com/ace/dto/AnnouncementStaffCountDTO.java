package com.ace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class AnnouncementStaffCountDTO {
    private Integer announcementId;
    private String title;
    private Date created_at;
    private Long staffCount;
}
