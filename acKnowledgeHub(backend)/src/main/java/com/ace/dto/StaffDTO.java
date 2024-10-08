package com.ace.dto;

import com.ace.entity.Company;
import com.ace.entity.Department;
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
    private String position;
    private Department department;
    private Company company;
}
