package com.atoss.idea.management.system.exception;

public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException() {
    }

    public UserAlreadyExistException(String message) {
        super(message);
    }
}
