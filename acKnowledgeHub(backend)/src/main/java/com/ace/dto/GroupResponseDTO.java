package com.ace.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GroupResponseDTO {
    private int id;
    private String name;
    private String status;

    public GroupResponseDTO(int id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }
}
