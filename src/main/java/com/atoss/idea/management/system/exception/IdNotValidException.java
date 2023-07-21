package com.atoss.idea.management.system.exception;

public class IdNotValidException extends RuntimeException {

    public IdNotValidException() {

    }

    public IdNotValidException(String message) {
        super(message);
    }

    public IdNotValidException(String message, Throwable cause) {
        super(message, cause);
    }
}