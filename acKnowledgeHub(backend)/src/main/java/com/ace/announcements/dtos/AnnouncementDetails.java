package com.ace.announcements.dtos;

import com.ace.entity.Announcement.Announcement;
import com.ace.entity.organization.Group;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AnnouncementDetails {
    private Announcement announcement;
    private List<Group> groups;
}
