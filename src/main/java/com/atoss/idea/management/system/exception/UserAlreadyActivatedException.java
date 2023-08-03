package com.atoss.idea.management.system.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserAlreadyActivatedException extends RuntimeException {

    /**
     * Custom exception class to indicate that a user already is activated
     *
     * @param message The error message explaining the reason for the exception
     */

    public UserAlreadyActivatedException(String message) {
        super(message);
    }
}
