package com.ace.dto;


import com.ace.entity.Category;
import com.ace.entity.Staff;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnnouncementUpdateDTO {
    private int id;
    private String title;
    private Date createdAt = new Date();
    private LocalDateTime scheduleAt;
    private String file;
    private String description;
    private String status = "active";
    private String createStaff;
    private Category category;
    private List<Integer> group;
    //    private List<String> department;
//    private List<String> company;
    private List<Staff> staff;
    private byte groupStatus;
    private int forRequest;
    private Integer createdStaffId;
    private Set<Integer> staffInGroups;
}
