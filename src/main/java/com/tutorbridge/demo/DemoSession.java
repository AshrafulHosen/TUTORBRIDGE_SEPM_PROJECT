package com.tutorbridge.demo;

import com.tutorbridge.application.Application;
import com.tutorbridge.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;

/**
 * A demo class between a guardian and a shortlisted tutor (1:1 with {@link Application}).
 */
@Entity
@Table(name = "demo_sessions")
public class DemoSession extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    private Application application;

    @Column(name = "scheduled_at", nullable = false)
    private Instant scheduledAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DemoMode mode;

    @Column(length = 200)
    private String location;

    @Column(length = 500)
    private String notes;

    protected DemoSession() {
    }

    public DemoSession(Application application, Instant scheduledAt, DemoMode mode, String location, String notes) {
        this.application = Objects.requireNonNull(application, "application");
        reschedule(scheduledAt, mode, location, notes);
    }

    public void reschedule(Instant scheduledAt, DemoMode mode, String location, String notes) {
        this.scheduledAt = Objects.requireNonNull(scheduledAt, "scheduledAt");
        this.mode = Objects.requireNonNull(mode, "mode");
        this.location = location;
        this.notes = notes;
    }

    public Application getApplication() {
        return application;
    }

    public Instant getScheduledAt() {
        return scheduledAt;
    }

    public DemoMode getMode() {
        return mode;
    }

    public String getLocation() {
        return location;
    }

    public String getNotes() {
        return notes;
    }
}
