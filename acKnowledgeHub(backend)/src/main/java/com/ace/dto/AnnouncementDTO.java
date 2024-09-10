package com.ace.dto;

import com.ace.entity.*;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementDTO {
    private int id;
    private String title;
    private Date createdAt = new Date();
    private LocalDateTime scheduleAt;
    private String file;
    private String description;
    private  byte groupStatus;
    private String status = "active";
    private Staff createStaff;
    private Category category;
    private List<Group> group;
    private List<Department> department;
    private List<Company> company;
    private List<Integer> staffIds;
    private List<Integer> groupIds;
    private int forRequest;


    private byte groupStatus;
    private int forRequest;
}
