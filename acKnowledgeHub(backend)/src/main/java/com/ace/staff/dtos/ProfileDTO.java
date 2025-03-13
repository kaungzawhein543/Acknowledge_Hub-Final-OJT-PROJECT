package com.ace.staff.dtos;

import com.ace.utility.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {

    private int id;
    private String name;
    private String companyStaffId;
    private String email;
    private String password;
    private boolean status;
    private Role role;
    private String photoPath;
    private String position;
    private String department;
    private String company;
    private LocalDateTime createdAt;
    private String chatId;
    private Map<String, Long> monthlyCount;

}
