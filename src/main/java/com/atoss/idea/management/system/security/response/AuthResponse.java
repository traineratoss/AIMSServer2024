package com.atoss.idea.management.system.security.response;

import com.atoss.idea.management.system.repository.dto.UserSecurityDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class AuthResponse {
    private Date accessTokenExpiryDate;
    private Date refreshTokenExpiryDate;
    private UserSecurityDTO userData;
}
