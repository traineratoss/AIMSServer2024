package com.atoss.idea.management.system.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserAlreadyDeactivatedException extends RuntimeException {

    /**
     *  Custom exception class to indicate that a user already is deactivated
     *
     * @param message The error message explaining the reason for the exception
     */
    public UserAlreadyDeactivatedException(String message) {
        super(message);
    }
}