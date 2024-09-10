package com.ace.dto;

import lombok.Data;

import java.util.List;

@Data
public class NotificationDTO {
    private int id;
    private String title;
    private String description;
    private String status;
    private String staffId;
    private int announceId;
    private List<Integer> groupIds;
    private AnnouncementDetails announcementDetails;

    public NotificationDTO( String title, String description, String staffId, int announceId, List<Integer> groupIds) {
        this.title = title;
        this.description = description;
        this.staffId = staffId;
        this.announceId = announceId;
        this.groupIds = groupIds;
    }

    public NotificationDTO() {}
}
