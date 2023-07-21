package com.atoss.idea.management.system.exception;

public class IdeaNotFoundException extends RuntimeException {

    public IdeaNotFoundException() {

    }

    public IdeaNotFoundException(String message) {
        super(message);
    }
}