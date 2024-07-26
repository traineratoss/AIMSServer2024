package com.atoss.idea.management.system.exception;

public class InvalidRefreshTokenException extends RuntimeException {

    /**
     * Custom exception class to indicate that the refresh token is invalid.
     *
     * @param message The error message explaining the reason for the exception
     */
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
