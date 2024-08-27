package com.ace.dto;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String staffId;
    private String oldPassword;
    private String newPassword;
}
