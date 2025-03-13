package com.ace.admin.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffSummaryDTO {
    private long totalStaff;
    private long activeStaff;
    private long inactiveStaff;
}
