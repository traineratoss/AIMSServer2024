package com.atoss.idea.management.system.exception;

import com.atoss.idea.management.system.repository.entity.RefreshToken;
import lombok.Getter;

@Getter
public class RefreshTokenExpiredException extends RuntimeException {

    private final RefreshToken refreshToken;

    /**
     * Custom exception class to indicate that the refresh token is expired.
     *
     * @param refreshToken - The expired refresh token
     * @param message The error message explaining the reason for the exception
     */
    public RefreshTokenExpiredException(RefreshToken refreshToken, String message) {
        super(message);
        this.refreshToken = refreshToken;
    }
}
