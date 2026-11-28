package com.tutorbridge.application;

import com.tutorbridge.application.state.ApplicationState;
import com.tutorbridge.application.state.ApplicationStateFactory;
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

/**
 * A tutor's application to a tuition post, tracking match score and lifecycle state.
 */
@Entity
@Table(
        name = "applications",
        uniqueConstraints = @UniqueConstraint(name = "uq_applications_post_tutor", columnNames = {"post_id", "tutor_id"}))
public class Application extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private TuitionPost post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tutor_id", nullable = false)
    private User tutor;

    @Column(name = "cover_note", length = 1000)
    private String coverNote;

    @Column(name = "match_score", nullable = false)
    private int matchScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    protected Application() {
    }

    public Application(TuitionPost post, User tutor, String coverNote, int matchScore) {
        this.post = post;
        this.tutor = tutor;
        this.coverNote = coverNote;
        this.matchScore = matchScore;
        this.status = ApplicationStatus.APPLIED;
    }

    /** Updates the application status; called by State implementations. */
    public void transitionTo(ApplicationStatus next) {
        this.status = next;
    }

    /** Returns the behavior object for the application's current lifecycle status. */
    public ApplicationState state() {
        return ApplicationStateFactory.from(status);
    }

    public TuitionPost getPost() {
        return post;
    }

    public void setPost(TuitionPost post) {
        this.post = post;
    }

    public User getTutor() {
        return tutor;
    }

    public void setTutor(User tutor) {
        this.tutor = tutor;
    }

    public String getCoverNote() {
        return coverNote;
    }

    public void setCoverNote(String coverNote) {
        this.coverNote = coverNote;
    }

    public int getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }

    public ApplicationStatus getStatus() {
        return status;
    }
}
