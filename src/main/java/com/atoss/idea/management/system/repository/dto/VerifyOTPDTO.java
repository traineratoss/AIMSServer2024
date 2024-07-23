package com.atoss.idea.management.system.repository.dto;

import lombok.Data;

@Data
public class VerifyOTPDTO {
    private String usernameOrEmail;
    private String otpCode;
}
