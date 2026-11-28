package com.tutorbridge.application.state;

import com.tutorbridge.application.Application;
import com.tutorbridge.common.InvalidTransitionException;

/**
 * Rejects every transition by default so concrete states only opt into legal actions.
 */
public abstract class AbstractApplicationState implements ApplicationState {

    @Override
    public void shortlist(Application application) {
        invalid(application, "shortlisted");
    }

    @Override
    public void scheduleDemo(Application application) {
        invalid(application, "scheduled for a demo");
    }

    @Override
    public void cancelDemo(Application application) {
        invalid(application, "cancelled");
    }

    @Override
    public void accept(Application application) {
        invalid(application, "accepted");
    }

    @Override
    public void reject(Application application) {
        invalid(application, "rejected");
    }

    @Override
    public void withdraw(Application application) {
        invalid(application, "withdrawn");
    }

    private void invalid(Application application, String action) {
        throw new InvalidTransitionException(
                "This application is " + application.getStatus()
                        + ", so it can't be " + action
                        + ". Refresh to see its latest status.");
    }
}
