package com.ace.staff.dtos;

import com.ace.utility.enums.Role;
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
