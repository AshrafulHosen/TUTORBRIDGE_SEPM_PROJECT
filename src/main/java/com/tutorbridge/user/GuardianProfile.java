package com.tutorbridge.user;

import com.tutorbridge.catalog.Area;
import com.tutorbridge.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Objects;

/**
 * A guardian's profile (1:1 with {@link User}, M:1 with their home {@link Area}).
 */
@Entity
@Table(name = "guardian_profiles")
public class GuardianProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_area_id")
    private Area homeArea;

    @Column(length = 200)
    private String address;

    protected GuardianProfile() {
    }

    public GuardianProfile(User user) {
        this.user = Objects.requireNonNull(user, "user");
    }

    public void updateDetails(Area homeArea, String address) {
        this.homeArea = homeArea;
        this.address = address;
    }

    public User getUser() {
        return user;
    }

    public Area getHomeArea() {
        return homeArea;
    }

    public String getAddress() {
        return address;
    }
}
