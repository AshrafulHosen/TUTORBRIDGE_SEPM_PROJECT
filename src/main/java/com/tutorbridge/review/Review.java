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

/**
 * Guardian review for an accepted tutor on a filled tuition post.
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
        this.post = post;
        this.guardian = guardian;
        this.tutor = tutor;
        this.rating = rating;
        this.comment = comment;
    }

    public TuitionPost getPost() {
        return post;
    }

    public void setPost(TuitionPost post) {
        this.post = post;
    }

    public User getGuardian() {
        return guardian;
    }

    public void setGuardian(User guardian) {
        this.guardian = guardian;
    }

    public User getTutor() {
        return tutor;
    }

    public void setTutor(User tutor) {
        this.tutor = tutor;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
