package com.tutorbridge.application.state;

import com.tutorbridge.application.Application;
import com.tutorbridge.application.ApplicationStatus;

/** State behavior for an application with a scheduled demo session. */
public final class DemoScheduledState extends AbstractApplicationState {

    @Override
    public void cancelDemo(Application application) {
        application.transitionTo(ApplicationStatus.SHORTLISTED);
    }

    @Override
    public void accept(Application application) {
        application.transitionTo(ApplicationStatus.ACCEPTED);
    }

    @Override
    public void reject(Application application) {
        application.transitionTo(ApplicationStatus.REJECTED);
    }
}
