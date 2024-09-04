package com.ace.dto;

import com.ace.enums.Role;
import lombok.Data;

@Data
public class StaffResponseDTO {
    private Integer id;
    private String companyStaffId;
    private String name;
    private String email;
    private Role role;
    private String position;
    private String department;
    private String company;
    private String status;

    public StaffResponseDTO(Integer id, String companyStaffId, String name, String email, Role role, String position, String department, String company, String status) {
        this.id = id;
        this.companyStaffId = companyStaffId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.position = position;
        this.department = department;
        this.company = company;
        this.status = status;
    }
}
