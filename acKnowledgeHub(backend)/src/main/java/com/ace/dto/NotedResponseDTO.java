package com.ace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class NotedResponseDTO {
    private String staffId;
    private String name;
    private String departmentName;
    private String companyName;
    private String positionName;
    private LocalDateTime createdAt;
    private Timestamp notedAt;
    private String email;

    public NotedResponseDTO(String staffId, String name, String departmentName, String companyName, String positionName,LocalDateTime createdAt, Timestamp notedAt, String email) {
        this.staffId = staffId;
        this.name = name;
        this.departmentName = departmentName;
        this.companyName = companyName;
        this.positionName = positionName;
        this.createdAt = createdAt;
        this.notedAt = notedAt;
        this.email = email;
    }
}