package com.atoss.idea.management.system.exception;

public class EmailFailedException extends RuntimeException {
    public EmailFailedException() {

    }

    public EmailFailedException(String message) {
        super(message);
    }

}
