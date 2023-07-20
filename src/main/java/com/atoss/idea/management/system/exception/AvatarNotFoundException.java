package com.atoss.idea.management.system.exception;

public class AvatarNotFoundException extends RuntimeException {
    public AvatarNotFoundException() {
    }

    public AvatarNotFoundException(String message) {
        super(message);
    }
}
