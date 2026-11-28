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

/**
 * Guardian-specific profile storing home neighbourhood and address details.
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

    @Column(name = "address_line", length = 255)
    private String addressLine;

    protected GuardianProfile() {
    }

    public GuardianProfile(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Area getHomeArea() {
        return homeArea;
    }

    public void setHomeArea(Area homeArea) {
        this.homeArea = homeArea;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }
}
