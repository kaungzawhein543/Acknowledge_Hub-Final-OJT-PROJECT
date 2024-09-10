package com.ace.dto;

import com.ace.enums.Role;
import lombok.Data;

@Data
public class StaffDTO {
    private int id;
    private String name;
    private String companyStaffId;
    private String email;
    private Role role;
}
