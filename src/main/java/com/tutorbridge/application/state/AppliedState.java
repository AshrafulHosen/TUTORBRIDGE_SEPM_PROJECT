package com.tutorbridge.application.state;

import com.tutorbridge.application.Application;
import com.tutorbridge.application.ApplicationStatus;

/** State behavior for a newly submitted application. */
public final class AppliedState extends AbstractApplicationState {

    @Override
    public void shortlist(Application application) {
        application.transitionTo(ApplicationStatus.SHORTLISTED);
    }

    @Override
    public void reject(Application application) {
        application.transitionTo(ApplicationStatus.REJECTED);
    }

    @Override
    public void withdraw(Application application) {
        application.transitionTo(ApplicationStatus.WITHDRAWN);
    }
}
