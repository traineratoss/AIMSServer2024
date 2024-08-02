package com.atoss.idea.management.system.security.token;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RefreshTokenConfig implements TokenConfig {

    @Value("${aims.app.jwt.refreshTokenExpiryMs}")
    private Long expiryMs;

    @Override
    public long getExpiryMs() {
        return expiryMs;
    }

    @Override
    public long getExpirySeconds() {
        return getExpiryMs() / 1000 + 1;
    }

    @Override
    public String getPath() {
        return "/api/v1/auth";
    }

    @Override
    public String getType() {
        return "refreshToken";
    }
}
