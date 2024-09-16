package com.ace.dto;

import com.ace.entity.Company;
import com.ace.entity.Department;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StaffGroupDTO {
    private Integer staffId;
    private String name;
    private String position;
    private Department department;
    private String photoPath;
    private Company company;

    public StaffGroupDTO(Integer staffId, String name, String position, Department department,String photoPath,Company company) {
        this.staffId = staffId;
        this.name = name;
        this.position = position;
        this.department = department;
        this.photoPath = photoPath;
        this.company = company;
    }
}