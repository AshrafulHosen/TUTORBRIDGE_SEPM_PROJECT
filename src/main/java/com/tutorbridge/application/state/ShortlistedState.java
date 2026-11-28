package com.tutorbridge.application.state;

import com.tutorbridge.application.Application;
import com.tutorbridge.application.ApplicationStatus;

/** State behavior for an application selected for further consideration. */
public final class ShortlistedState extends AbstractApplicationState {

    @Override
    public void scheduleDemo(Application application) {
        application.transitionTo(ApplicationStatus.DEMO_SCHEDULED);
    }

    @Override
    public void reject(Application application) {
        application.transitionTo(ApplicationStatus.REJECTED);
    }
}
