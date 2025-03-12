package com.ace.dto;

import com.ace.entity.Company;
import com.ace.entity.Department;
import com.ace.entity.Position;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StaffGroupDTO {
    private Integer staffId;
    private String name;
    private Position position;
    private Department department;
    private String photoPath;
    private Company company;

    public StaffGroupDTO(Integer staffId, String name, Position position, Department department,String photoPath,Company company) {
        this.staffId = staffId;
        this.name = name;
        this.position = position;
        this.department = department;
        this.photoPath = photoPath;
        this.company = company;
    }
}