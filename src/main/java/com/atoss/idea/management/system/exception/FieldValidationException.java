package com.atoss.idea.management.system.exception;

public class FieldValidationException extends RuntimeException {

    public FieldValidationException() {

    }

    public FieldValidationException(String message) {
        super(message);
    }
}