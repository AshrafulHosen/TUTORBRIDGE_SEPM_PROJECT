package com.tutorbridge.application;

/**
 * Lifecycle states of a tutor's application to a tuition post.
 */
public enum ApplicationStatus {
    APPLIED,
    SHORTLISTED,
    DEMO_SCHEDULED,
    ACCEPTED,
    REJECTED,
    WITHDRAWN;

    /**
     * Returns whether this status can still participate in the application workflow.
     */
    public boolean isActive() {
        return this == APPLIED || this == SHORTLISTED || this == DEMO_SCHEDULED;
    }

    /**
     * Returns whether reaching this status reveals the tutor's phone number to the guardian.
     */
    public boolean revealsPhone() {
        return this == SHORTLISTED || this == DEMO_SCHEDULED || this == ACCEPTED;
    }
}
