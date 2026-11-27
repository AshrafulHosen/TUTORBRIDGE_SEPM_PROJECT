package com.tutorbridge.common;

/**
 * Thrown when an application lifecycle transition is illegal in the current state (HTTP 409).
 */
public class InvalidTransitionException extends ConflictException {

    public InvalidTransitionException(String message) {
        super(message);
    }
}
