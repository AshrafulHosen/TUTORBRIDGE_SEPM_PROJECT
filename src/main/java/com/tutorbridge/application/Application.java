package com.tutorbridge.application;

import com.tutorbridge.common.BaseEntity;
import com.tutorbridge.tuition.TuitionPost;
import com.tutorbridge.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Objects;

/**
 * A tutor's application to a tuition post (M:1 post, M:1 tutor; one per post and tutor).
 */
@Entity
@Table(name = "applications",
        uniqueConstraints = @UniqueConstraint(name = "uq_applications_post_tutor",
                columnNames = {"post_id", "tutor_id"}))
public class Application extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private TuitionPost post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tutor_id", nullable = false)
    private User tutor;

    @Column(name = "cover_note", length = 500)
    private String coverNote;

    @Column(name = "match_score", nullable = false)
    private int matchScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    protected Application() {
    }

    public Application(TuitionPost post, User tutor, String coverNote, int matchScore) {
        this.post = Objects.requireNonNull(post, "post");
        this.tutor = Objects.requireNonNull(tutor, "tutor");
        this.coverNote = coverNote;
        this.matchScore = matchScore;
    }

    /**
     * Moves the application to {@code next} and returns the previous status. Callers go through
     * the State pattern, which decides whether the move is legal.
     */
    public ApplicationStatus transitionTo(ApplicationStatus next) {
        ApplicationStatus previous = status;
        this.status = Objects.requireNonNull(next, "status");
        return previous;
    }

    public boolean belongsToTutor(Long userId) {
        return tutor.getId() != null && tutor.getId().equals(userId);
    }

    public TuitionPost getPost() {
        return post;
    }

    public User getTutor() {
        return tutor;
    }

    public String getCoverNote() {
        return coverNote;
    }

    public int getMatchScore() {
        return matchScore;
    }

    public ApplicationStatus getStatus() {
        return status;
    }
}
