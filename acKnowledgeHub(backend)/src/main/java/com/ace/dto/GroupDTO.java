package com.ace.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class GroupDTO {
    private int id;
    private String name;
    private String status;
    private Date createdAt;
    private List<StaffDTO> staff;
}
