package com.atoss.idea.management.system.exception;

public class IdeaNotFoundException extends RuntimeException {

    /**
     *  Default constructor.
     */

    public IdeaNotFoundException() {

    }
    /**
     *   Constructor with a specified detail message.
     *
     * @param message The detail message
     */

    public IdeaNotFoundException(String message) {
        super(message);
    }
}