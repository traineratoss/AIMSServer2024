package com.atoss.idea.management.system.repository.dto;

import lombok.Data;

@Data
public class VerifyOtpDTO {
    private String usernameOrEmail;
    private String otpCode;
}
