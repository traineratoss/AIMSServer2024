package com.atoss.idea.management.system.exception;

public class InsufficientReportsException extends RuntimeException {

    /**
     * Constructs a new {@code InsufficientReportsException} with the specified detail message.
     *
     * @param msg the detail message, which is saved for later retrieval by the {@link Throwable#getMessage()} method
     */
    public InsufficientReportsException(String msg) {
        super(msg);
    }
}
