package com.tutorbridge.user;

import com.tutorbridge.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Objects;

/**
 * A guardian's profile (1:1 with {@link User}).
 */
@Entity
@Table(name = "guardian_profiles")
public class GuardianProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 200)
    private String address;

    protected GuardianProfile() {
    }

    public GuardianProfile(User user) {
        this.user = Objects.requireNonNull(user, "user");
    }

    public User getUser() {
        return user;
    }

    public String getAddress() {
        return address;
    }
}
