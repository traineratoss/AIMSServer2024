package com.atoss.idea.management.system.exception;

public class FieldValidationException extends RuntimeException {
    /**
     *  Default constructor.
     */


    public FieldValidationException() {

    }

    /**
     *   Constructor with a specified detail message.
     *
     * @param message The detail message
     */

    public FieldValidationException(String message) {
        super(message);
    }

    /**
     * Constructor with a specified detail message and cause
     *
     * @param message The detail message
     * @param cause  The cause for witch user has an error message
     */

    public FieldValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}