package com.ace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementStatsDTO {
    private long totalAnnouncements;
    private long publishedAnnouncements;
    private long unpublishedAnnouncements;
}
