package com.ace.dto;

import com.ace.entity.Announcement;
import com.ace.entity.Group;
import com.ace.entity.Staff;
import com.ace.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {

    private int id;
    private String name;
    private String companyStaffId;
    private String email;
    private String status;
    private Role role;
    private String position;
    private String department;
    private String company;
    private Date createdAt;
    private String chatId;
}
