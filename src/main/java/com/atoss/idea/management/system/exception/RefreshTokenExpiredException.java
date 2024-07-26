package com.atoss.idea.management.system.exception;

public class RefreshTokenExpiredException extends RuntimeException {

    /**
     * Custom exception class to indicate that the refresh token is expired.
     *
     * @param message The error message explaining the reason for the exception
     */
    public RefreshTokenExpiredException(String message) {
        super(message);
    }
}
