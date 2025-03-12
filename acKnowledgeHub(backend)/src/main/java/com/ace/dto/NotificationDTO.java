package com.ace.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class NotificationDTO {
    private int id;
    private String title;
    private String description;
    private String status;
    private String staffId;
    private String url;
    private boolean checked;
    private LocalDateTime created_at;
    private int announceId;
    private List<Integer> groupIds;
    private AnnouncementDetails announcementDetails;

    public NotificationDTO(Integer id , String title, String description, String staffId,boolean checked,String url, LocalDateTime created_at, int announceId, List<Integer> groupIds,String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.staffId = staffId;
        this.checked = checked;
        this.url = url;
        this.created_at = created_at;
        this.announceId = announceId;
        this.groupIds = groupIds;
        this.status = status;
    }

    public NotificationDTO() {}
}
