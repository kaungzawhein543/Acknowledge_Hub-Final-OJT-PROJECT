package com.ace.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO {
    private int id;
    private String name;
    private String status;
    private LocalDateTime createdAt;
    private List<StaffDTO> staff;
}
