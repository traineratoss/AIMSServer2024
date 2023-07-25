package com.atoss.idea.management.system.exception;

public class EmailAlreadyExistException extends RuntimeException {
    public EmailAlreadyExistException() {
    }

    public EmailAlreadyExistException(String message) {
        super(message);
    }
}
