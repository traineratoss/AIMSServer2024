package com.atoss.idea.management.system.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SubscriptionNotFoundException extends RuntimeException {

    /**
     *   Constructor with a specified detail message.
     *
     * @param message The detail message
     */
    public SubscriptionNotFoundException(String message) {
        super(message);
    }
}
