package com.tutorbridge.application.state;

import com.tutorbridge.application.Application;

/**
 * Defines the lifecycle actions available for an application.
 *
 * <p><strong>Why this pattern (State):</strong> Each application status owns its legal
 * transitions, keeping status-specific rules out of services and preventing callers from
 * bypassing the lifecycle. Adding or reviewing a transition therefore stays local to the
 * state involved instead of growing a shared conditional block.
 *
 * <pre>
 * APPLIED -------- shortlist --------&gt; SHORTLISTED ---- scheduleDemo ----&gt; DEMO_SCHEDULED
 *    |                                      |                                  |
 *    +-------- reject --------&gt; REJECTED &lt;--+--------------- reject -----------+
 *    |
 *    +------ withdraw -------&gt; WITHDRAWN         DEMO_SCHEDULED -- accept --&gt; ACCEPTED
 *
 * DEMO_SCHEDULED -- cancelDemo --&gt; SHORTLISTED
 * </pre>
 */
public interface ApplicationState {

    void shortlist(Application application);

    void scheduleDemo(Application application);

    void cancelDemo(Application application);

    void accept(Application application);

    void reject(Application application);

    void withdraw(Application application);
}
