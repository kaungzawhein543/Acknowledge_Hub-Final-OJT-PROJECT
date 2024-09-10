package com.ace.dto;

import com.ace.entity.Staff;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StaffPageResponse {
    private List<Staff> data;
    private String hasMore;

}

