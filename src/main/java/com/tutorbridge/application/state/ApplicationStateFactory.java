package com.tutorbridge.application.state;

import com.tutorbridge.application.ApplicationStatus;
import java.util.Objects;

/** Resolves the behavior object corresponding to an application status. */
public final class ApplicationStateFactory {

    private static final ApplicationState APPLIED = new AppliedState();
    private static final ApplicationState SHORTLISTED = new ShortlistedState();
    private static final ApplicationState DEMO_SCHEDULED = new DemoScheduledState();
    private static final ApplicationState ACCEPTED = new AcceptedState();
    private static final ApplicationState REJECTED = new RejectedState();
    private static final ApplicationState WITHDRAWN = new WithdrawnState();

    private ApplicationStateFactory() {
    }

    public static ApplicationState from(ApplicationStatus status) {
        return switch (Objects.requireNonNull(status, "status must not be null")) {
            case APPLIED -> APPLIED;
            case SHORTLISTED -> SHORTLISTED;
            case DEMO_SCHEDULED -> DEMO_SCHEDULED;
            case ACCEPTED -> ACCEPTED;
            case REJECTED -> REJECTED;
            case WITHDRAWN -> WITHDRAWN;
        };
    }
}
