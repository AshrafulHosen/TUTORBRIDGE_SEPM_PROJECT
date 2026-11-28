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

/**
 * Scheduled trial/demo session linked 1:1 to a shortlisted application.
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

    @Column(nullable = false, length = 200)
    private String location;

    @Column(length = 500)
    private String notes;

    protected DemoSession() {
    }

    public DemoSession(Application application, Instant scheduledAt, DemoMode mode, String location, String notes) {
        this.application = application;
        this.scheduledAt = scheduledAt;
        this.mode = mode;
        this.location = location;
        this.notes = notes;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Instant getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(Instant scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public DemoMode getMode() {
        return mode;
    }

    public void setMode(DemoMode mode) {
        this.mode = mode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
