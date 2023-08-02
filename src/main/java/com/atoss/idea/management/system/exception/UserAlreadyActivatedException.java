package com.atoss.idea.management.system.exception;

public class UserAlreadyActivatedException extends RuntimeException {
    public UserAlreadyActivatedException() {
    }

    public UserAlreadyActivatedException(String message) {
        super(message);
    }
}
