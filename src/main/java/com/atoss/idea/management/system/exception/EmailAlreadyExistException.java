package com.atoss.idea.management.system.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EmailAlreadyExistException extends RuntimeException {

    /**
     * Custom exception class to indicate that the email already exists in the system
     *
     * @param message The error message explaining the reason for the exception
     */
    public EmailAlreadyExistException(String message) {
        super(message);
    }
}
