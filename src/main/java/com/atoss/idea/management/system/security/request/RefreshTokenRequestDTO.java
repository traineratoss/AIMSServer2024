package com.atoss.idea.management.system.security.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RefreshTokenRequestDTO {
    private String token;
}
