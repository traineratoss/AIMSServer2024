package com.atoss.idea.management.system.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SubscriptionNotFoundException extends RuntimeException {
    public SubscriptionNotFoundException(String message) {super(message); }
}
