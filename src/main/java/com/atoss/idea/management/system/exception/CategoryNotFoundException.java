package com.atoss.idea.management.system.exception;

public class CategoryNotFoundException extends RuntimeException {

    /**
     *  Default constructor.
     */
    public CategoryNotFoundException() {

    }

    /**
     *   Constructor with a specified detail message.
     *
     * @param message The detail message
     */
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
