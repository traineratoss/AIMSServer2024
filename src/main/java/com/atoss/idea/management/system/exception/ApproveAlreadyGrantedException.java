package com.atoss.idea.management.system.exception;

public class ApproveAlreadyGrantedException extends RuntimeException {
    public ApproveAlreadyGrantedException() {
    }

    public ApproveAlreadyGrantedException(String message) {
        super(message);
    }
}
