package com.ace.dto;


import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO {
    private int id;
    private String name;
    private String status;
    private Date createdAt;
    private List<StaffDTO> staff;
}
