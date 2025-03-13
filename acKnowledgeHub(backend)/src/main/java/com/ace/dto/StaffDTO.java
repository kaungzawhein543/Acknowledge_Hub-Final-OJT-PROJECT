package com.ace.dto;

import com.ace.entity.organization.Company;
import com.ace.entity.organization.Position;
import com.ace.entity.organization.Department;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffDTO {
    private int id;
    private String staffId;
    private String name;
    private Position position;
    private Department department;
    private Company company;
    private String photoPath;
}
