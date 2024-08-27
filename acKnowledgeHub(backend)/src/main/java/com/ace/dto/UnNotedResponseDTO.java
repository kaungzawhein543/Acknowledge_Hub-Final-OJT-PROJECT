package com.ace.dto;

import lombok.Data;

@Data
public class UnNotedResponseDTO {
    private String staffId;
    private String name;
    private String departmentName;
    private String companyName;
    private String positionName;
    private String email;

    public UnNotedResponseDTO(String staffId, String name, String departmentName, String companyName, String positionName, String email) {
        this.staffId = staffId;
        this.name = name;
        this.departmentName = departmentName;
        this.companyName = companyName;
        this.positionName = positionName;
        this.email = email;
    }
}