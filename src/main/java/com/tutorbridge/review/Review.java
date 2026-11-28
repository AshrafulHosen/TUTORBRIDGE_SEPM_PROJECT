package com.tutorbridge.review;

import com.tutorbridge.common.BaseEntity;
import com.tutorbridge.tuition.TuitionPost;
import com.tutorbridge.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Objects;

/**
 * A guardian's rating of the accepted tutor (1:1 with the filled {@link TuitionPost};
 * M:1 with the guardian and the tutor).
 */
@Entity
@Table(name = "reviews")
public class Review extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false, unique = true)
    private TuitionPost post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guardian_id", nullable = false)
    private User guardian;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tutor_id", nullable = false)
    private User tutor;

    @Column(nullable = false)
    private int rating;

    @Column(length = 500)
    private String comment;

    protected Review() {
    }

    public Review(TuitionPost post, User guardian, User tutor, int rating, String comment) {
        this.post = Objects.requireNonNull(post, "post");
        this.guardian = Objects.requireNonNull(guardian, "guardian");
        this.tutor = Objects.requireNonNull(tutor, "tutor");
        edit(rating, comment);
    }

    public void edit(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    public boolean isWrittenBy(Long userId) {
        return guardian.getId() != null && guardian.getId().equals(userId);
    }

    public TuitionPost getPost() {
        return post;
    }

    public User getGuardian() {
        return guardian;
    }

    public User getTutor() {
        return tutor;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }
}
