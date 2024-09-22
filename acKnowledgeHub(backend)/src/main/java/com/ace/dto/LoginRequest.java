package com.ace.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String staffId;
    private String password;
    private boolean rememberMe;
}
