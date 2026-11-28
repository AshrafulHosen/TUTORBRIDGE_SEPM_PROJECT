package com.tutorbridge.application;

/**
 * Lifecycle status of a tutor's application to a tuition post. Which moves between statuses are
 * legal is decided by the State pattern classes in {@code application.state}.
 */
public enum ApplicationStatus {
    APPLIED,
    SHORTLISTED,
    DEMO_SCHEDULED,
    ACCEPTED,
    REJECTED,
    WITHDRAWN
}
