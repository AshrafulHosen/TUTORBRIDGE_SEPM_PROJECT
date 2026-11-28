package com.tutorbridge.application.state;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tutorbridge.application.Application;
import com.tutorbridge.application.ApplicationStatus;
import com.tutorbridge.common.InvalidTransitionException;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ApplicationStateTransitionTest {

    private static final Map<Transition, ApplicationStatus> LEGAL_TRANSITIONS = Map.of(
            new Transition(ApplicationStatus.APPLIED, Action.SHORTLIST), ApplicationStatus.SHORTLISTED,
            new Transition(ApplicationStatus.APPLIED, Action.REJECT), ApplicationStatus.REJECTED,
            new Transition(ApplicationStatus.APPLIED, Action.WITHDRAW), ApplicationStatus.WITHDRAWN,
            new Transition(ApplicationStatus.SHORTLISTED, Action.SCHEDULE_DEMO), ApplicationStatus.DEMO_SCHEDULED,
            new Transition(ApplicationStatus.SHORTLISTED, Action.REJECT), ApplicationStatus.REJECTED,
            new Transition(ApplicationStatus.DEMO_SCHEDULED, Action.CANCEL_DEMO), ApplicationStatus.SHORTLISTED,
            new Transition(ApplicationStatus.DEMO_SCHEDULED, Action.ACCEPT), ApplicationStatus.ACCEPTED,
            new Transition(ApplicationStatus.DEMO_SCHEDULED, Action.REJECT), ApplicationStatus.REJECTED);

    @ParameterizedTest(name = "{0} + {1} -> {2}")
    @MethodSource("allStatusAndActionPairs")
    void enforcesCompleteTransitionTable(
            ApplicationStatus initialStatus,
            Action action,
            ApplicationStatus expectedStatus) {
        Application application = applicationIn(initialStatus);

        if (expectedStatus != null) {
            action.perform(application.state(), application);

            assertThat(application.getStatus()).isEqualTo(expectedStatus);
            return;
        }

        assertThatThrownBy(() -> action.perform(application.state(), application))
                .isInstanceOf(InvalidTransitionException.class)
                .hasMessageStartingWith("This application is " + initialStatus + ", so it can't be ")
                .hasMessageEndingWith(". Refresh to see its latest status.");
        assertThat(application.getStatus()).isEqualTo(initialStatus);
    }

    @Test
    void factoryReturnsTheStateClassForEveryStatus() {
        assertThat(ApplicationStateFactory.from(ApplicationStatus.APPLIED)).isInstanceOf(AppliedState.class);
        assertThat(ApplicationStateFactory.from(ApplicationStatus.SHORTLISTED)).isInstanceOf(ShortlistedState.class);
        assertThat(ApplicationStateFactory.from(ApplicationStatus.DEMO_SCHEDULED))
                .isInstanceOf(DemoScheduledState.class);
        assertThat(ApplicationStateFactory.from(ApplicationStatus.ACCEPTED)).isInstanceOf(AcceptedState.class);
        assertThat(ApplicationStateFactory.from(ApplicationStatus.REJECTED)).isInstanceOf(RejectedState.class);
        assertThat(ApplicationStateFactory.from(ApplicationStatus.WITHDRAWN)).isInstanceOf(WithdrawnState.class);
    }

    @Test
    void applicationReturnsTheStateForItsCurrentStatus() {
        Application application = applicationIn(ApplicationStatus.APPLIED);

        assertThat(application.state()).isInstanceOf(AppliedState.class);
        application.state().shortlist(application);
        assertThat(application.state()).isInstanceOf(ShortlistedState.class);
    }

    @Test
    void activeAndPhoneVisibilityRulesMatchTheWorkflow() {
        assertThat(ApplicationStatus.APPLIED.isActive()).isTrue();
        assertThat(ApplicationStatus.SHORTLISTED.isActive()).isTrue();
        assertThat(ApplicationStatus.DEMO_SCHEDULED.isActive()).isTrue();
        assertThat(ApplicationStatus.ACCEPTED.isActive()).isFalse();
        assertThat(ApplicationStatus.REJECTED.isActive()).isFalse();
        assertThat(ApplicationStatus.WITHDRAWN.isActive()).isFalse();

        assertThat(ApplicationStatus.APPLIED.revealsPhone()).isFalse();
        assertThat(ApplicationStatus.SHORTLISTED.revealsPhone()).isTrue();
        assertThat(ApplicationStatus.DEMO_SCHEDULED.revealsPhone()).isTrue();
        assertThat(ApplicationStatus.ACCEPTED.revealsPhone()).isTrue();
        assertThat(ApplicationStatus.REJECTED.revealsPhone()).isFalse();
        assertThat(ApplicationStatus.WITHDRAWN.revealsPhone()).isFalse();
    }

    private static Stream<Arguments> allStatusAndActionPairs() {
        return Stream.of(ApplicationStatus.values())
                .flatMap(status -> Stream.of(Action.values())
                        .map(action -> Arguments.of(
                                status,
                                action,
                                LEGAL_TRANSITIONS.get(new Transition(status, action)))));
    }

    private static Application applicationIn(ApplicationStatus status) {
        Application application = new Application(null, null, null, 0);
        application.transitionTo(status);
        return application;
    }

    private record Transition(ApplicationStatus status, Action action) {
    }

    private enum Action {
        SHORTLIST {
            @Override
            void perform(ApplicationState state, Application application) {
                state.shortlist(application);
            }
        },
        SCHEDULE_DEMO {
            @Override
            void perform(ApplicationState state, Application application) {
                state.scheduleDemo(application);
            }
        },
        CANCEL_DEMO {
            @Override
            void perform(ApplicationState state, Application application) {
                state.cancelDemo(application);
            }
        },
        ACCEPT {
            @Override
            void perform(ApplicationState state, Application application) {
                state.accept(application);
            }
        },
        REJECT {
            @Override
            void perform(ApplicationState state, Application application) {
                state.reject(application);
            }
        },
        WITHDRAW {
            @Override
            void perform(ApplicationState state, Application application) {
                state.withdraw(application);
            }
        };

        abstract void perform(ApplicationState state, Application application);
    }
}
