package com.ace.dto;

import com.ace.enums.Role;
import lombok.Data;

@Data
public class StaffRequestDTO {
    private String companyStaffId;
    private String name;
    private String email;
    private Role role;
    private Integer positionId;
    private Integer departmentId;
    private Integer companyId;
}
