package com.atoss.idea.management.system.exception;

public class UserAlreadyDeactivatedException extends RuntimeException {
    public UserAlreadyDeactivatedException() {
    }

    public UserAlreadyDeactivatedException(String message) {
        super(message);
    }
}