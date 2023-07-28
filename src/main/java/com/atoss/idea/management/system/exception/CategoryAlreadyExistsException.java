package com.atoss.idea.management.system.exception;

public class CategoryAlreadyExistsException extends RuntimeException {

    public CategoryAlreadyExistsException() {

    }

    public CategoryAlreadyExistsException(String message) {
        super(message);
    }
}
