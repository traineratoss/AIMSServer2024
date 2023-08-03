package com.atoss.idea.management.system.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ApproveAlreadyGrantedException extends RuntimeException {
    /**
     * Custom exception class to indicate that the user already is approved
     *
     * @param message The error message explaining the reason for the exception
     */
    public ApproveAlreadyGrantedException(String message) {
        super(message);
    }
}
