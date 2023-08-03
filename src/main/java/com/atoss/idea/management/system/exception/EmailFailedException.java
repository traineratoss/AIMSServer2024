package com.atoss.idea.management.system.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EmailFailedException extends RuntimeException {

    /**
     * Custom exception class to indicate that the email failed to be sent
     *
     * @param message The error message explaining the reason for the exception
     */
    public EmailFailedException(String message) {
        super(message);
    }

}
