package com.atoss.idea.management.system.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class IncorrectPasswordException extends RuntimeException {

    /**
     * Custom exception class to indicate that the provided password is incorrect
     *
     * @param message The error message explaining the reason for the exception
     */
    public IncorrectPasswordException(String message) {
        super(message);
    }
}
