package com.ace.dto;

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
