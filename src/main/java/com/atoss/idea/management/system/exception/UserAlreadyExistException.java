package com.atoss.idea.management.system.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserAlreadyExistException extends RuntimeException {

    /**
     * Custom exception class to indicate that a user already exists in the system
     *
     * @param message The error message explaining the reason for the exception
     */
    public UserAlreadyExistException(String message) {
        super(message);
    }
}
