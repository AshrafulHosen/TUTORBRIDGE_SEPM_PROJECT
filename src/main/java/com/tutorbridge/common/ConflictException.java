package com.tutorbridge.common;

/**
 * Thrown when an operation conflicts with the current state or uniqueness constraints (HTTP 409).
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
