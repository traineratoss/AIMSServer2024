package com.atoss.idea.management.system.exception;

public class DocumentNotFoundException extends RuntimeException {
    /**
     *   Constructor with a specified detail message.
     *
     * @param message The detail message
     */
    public DocumentNotFoundException(String message) {
        super(message);
    }
}
