package com.tutorbridge.tuition;

/**
 * Lifecycle of a tuition post: OPEN → IN_REVIEW (first application) → FILLED (tutor accepted),
 * or CLOSED by the guardian.
 */
public enum PostStatus {
    OPEN,
    IN_REVIEW,
    FILLED,
    CLOSED;

    /** Whether tutors may still apply to a post in this status. */
    public boolean acceptsApplications() {
        return this == OPEN || this == IN_REVIEW;
    }
}
