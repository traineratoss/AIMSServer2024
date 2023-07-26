package com.atoss.idea.management.system.exception;

public class UserStatusIsActiveException extends RuntimeException {
    public UserStatusIsActiveException() {

    }

    public UserStatusIsActiveException(String message) {
        super(message);
    }
}
