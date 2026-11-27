package com.tutorbridge.common.event;

import java.time.Instant;
import java.util.List;

/**
 * Shared domain event contracts published across TutorBridge modules.
 *
 * <p><strong>Why this pattern (Observer):</strong> Core business services (posts, applications,
 * demos, and admin actions) publish immutable event records through Spring's
 * {@code ApplicationEventPublisher} instead of directly calling notification, audit, or metrics
 * services. Listeners react asynchronously/independently via
 * {@code @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)} with
 * {@code @Transactional(propagation = Propagation.REQUIRES_NEW)}, guaranteeing that side effects
 * never trigger for rolled-back transactions and new observers can be added without modifying
 * domain services.
 */
public final class DomainEvents {

    private DomainEvents() {
    }

    /** Published when a guardian creates a tuition post and matching tutors are identified. */
    public record PostPublished(
            Long postId,
            String postTitle,
            Long guardianId,
            List<Long> matchedTutorIds) {
        public PostPublished {
            matchedTutorIds = matchedTutorIds == null ? List.of() : List.copyOf(matchedTutorIds);
        }
    }

    /** Published when a tutor applies to a tuition post. */
    public record ApplicationSubmitted(
            Long applicationId,
            Long postId,
            String postTitle,
            Long guardianId,
            Long tutorId,
            String tutorName,
            int matchScore) {
    }

    /**
     * Published whenever an application transitions between lifecycle states.
     * {@code fromStatus} and {@code toStatus} hold {@code ApplicationStatus.name()} values.
     */
    public record ApplicationStatusChanged(
            Long applicationId,
            Long postId,
            String postTitle,
            Long guardianId,
            Long tutorId,
            String tutorName,
            String fromStatus,
            String toStatus) {
    }

    /** Published when a guardian schedules a demo session for a shortlisted application. */
    public record DemoScheduled(
            Long demoId,
            Long applicationId,
            Long postId,
            String postTitle,
            Long guardianId,
            Long tutorId,
            Instant scheduledAt,
            String mode,
            String location) {
    }

    /** Published when a guardian reschedules an existing demo session. */
    public record DemoRescheduled(
            Long demoId,
            Long applicationId,
            Long postId,
            String postTitle,
            Long guardianId,
            Long tutorId,
            Instant previousScheduledAt,
            Instant newScheduledAt,
            String mode,
            String location) {
    }

    /** Published when a guardian cancels a demo session. */
    public record DemoCancelled(
            Long demoId,
            Long applicationId,
            Long postId,
            String postTitle,
            Long guardianId,
            Long tutorId,
            Instant scheduledAt) {
    }

    /** Published when a guardian accepts a tutor, filling the post and rejecting other active applicants. */
    public record PostFilled(
            Long postId,
            String postTitle,
            Long guardianId,
            Long acceptedTutorId,
            List<Long> rejectedTutorIds) {
        public PostFilled {
            rejectedTutorIds = rejectedTutorIds == null ? List.of() : List.copyOf(rejectedTutorIds);
        }
    }

    /** Published when an administrator suspends a user account. */
    public record UserSuspended(
            Long userId,
            String email,
            Long adminId) {
    }

    /** Published whenever an administrator executes an audited action. */
    public record AdminAction(
            Long adminId,
            String action,
            String targetType,
            Long targetId,
            String details,
            Instant occurredAt) {
    }
}
