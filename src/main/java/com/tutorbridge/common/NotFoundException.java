package com.tutorbridge.common;

/**
 * Thrown when a requested resource does not exist or is not accessible to the caller (HTTP 404).
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
