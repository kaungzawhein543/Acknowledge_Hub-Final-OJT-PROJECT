package com.ace.dto;

import lombok.Data;

@Data
public  class StaffRequest {
    private String staffId;
    private String name;
    private String email;
    private String address;

}