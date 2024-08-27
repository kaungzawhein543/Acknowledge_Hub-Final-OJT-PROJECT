package com.ace.dto;

import com.ace.entity.Staff;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TestDTO {
    private Staff staff;
    private LocalDateTime notedAt;
}
