package com.ace.dto;

import com.ace.entity.Announcement;
import com.ace.entity.Group;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AnnouncementDetails {
    private Announcement announcement;
    private List<Group> groups;
}
