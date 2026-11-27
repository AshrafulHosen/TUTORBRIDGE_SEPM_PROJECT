package com.tutorbridge.common;

/**
 * Thrown when client input fails domain validation rules (HTTP 400).
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
