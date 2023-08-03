package com.atoss.idea.management.system.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UsernameAlreadyExistException extends RuntimeException {

    /**
     * Custom exception class to indicate that a username already exists in the system
     *
     * @param message The error message explaining the reason for the exception
     */
    public UsernameAlreadyExistException(String message) {
        super(message);
    }
}
