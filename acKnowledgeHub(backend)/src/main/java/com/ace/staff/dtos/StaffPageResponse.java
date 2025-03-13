package com.ace.staff.dtos;

import com.ace.entity.organization.Staff;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StaffPageResponse {
    private List<Staff> data;
    private String hasMore;

}

