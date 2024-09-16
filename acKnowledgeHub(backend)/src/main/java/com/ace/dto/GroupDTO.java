package com.ace.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GroupDTO {
    private int id;
    private String name;
    private String status;

    public GroupDTO(int id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }
}
