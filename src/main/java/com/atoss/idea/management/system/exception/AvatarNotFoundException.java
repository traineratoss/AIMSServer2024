package com.atoss.idea.management.system.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AvatarNotFoundException extends RuntimeException {

    /**
     * Custom exception class to indicate that the avatar doesn't exist in the system
     *
     * @param message The error message explaining the reason for the exception
     */
    public AvatarNotFoundException(String message) {
        super(message);
    }
}
