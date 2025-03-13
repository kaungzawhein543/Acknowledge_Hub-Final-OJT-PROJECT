package com.ace.thirdPartyNotifications.email.dtos;

import lombok.Data;

@Data
public class OTPEmailDTO {
    private String email;
    private String otp;
}
