package com.ace.dto;

import com.ace.entity.Department;
import lombok.Data;

@Data
public class StaffGroupDTO {
    private Integer staffId;
    private String name;
    private String position;
    private Department department;

    public StaffGroupDTO(Integer staffId, String name, String position, Department department) {
        this.staffId = staffId;
        this.name = name;
        this.position = position;
        this.department = department;
    }
}