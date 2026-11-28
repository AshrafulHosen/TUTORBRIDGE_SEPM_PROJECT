package com.tutorbridge.notification;

import com.tutorbridge.common.BaseEntity;
import com.tutorbridge.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;

/**
 * An in-app message for one user (M:1 recipient). {@code link} is a frontend hash route such as
 * {@code #post/12}.
 */
@Entity
@Table(name = "notifications")
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Column(nullable = false, length = 40)
    private String type;

    @Column(nullable = false, length = 300)
    private String message;

    @Column(length = 200)
    private String link;

    @Column(name = "is_read", nullable = false)
    private boolean read;

    protected Notification() {
    }

    public Notification(User recipient, String type, String message, String link) {
        this.recipient = Objects.requireNonNull(recipient, "recipient");
        this.type = Objects.requireNonNull(type, "type");
        this.message = Objects.requireNonNull(message, "message");
        this.link = link;
    }

    public void markRead() {
        this.read = true;
    }

    public User getRecipient() {
        return recipient;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getLink() {
        return link;
    }

    public boolean isRead() {
        return read;
    }
}
