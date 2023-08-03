package com.atoss.idea.management.system.exception;

public class CategoryAlreadyExistsException extends RuntimeException {
    /**
     *  Default constructor.
     */

    public CategoryAlreadyExistsException() {

    }
    /**
     *   Constructor with a specified detail message.
     *
     * @param message The detail message
     */

    public CategoryAlreadyExistsException(String message) {
        super(message);
    }
}
