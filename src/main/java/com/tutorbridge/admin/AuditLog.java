package com.tutorbridge.admin;

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
 * An immutable record of an administrator action (M:1 admin).
 */
@Entity
@Table(name = "audit_logs")
public class AuditLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    @Column(nullable = false, length = 50)
    private String action;

    @Column(name = "target_type", nullable = false, length = 30)
    private String targetType;

    @Column(name = "target_id")
    private Long targetId;

    @Column(length = 500)
    private String details;

    protected AuditLog() {
    }

    public AuditLog(User admin, String action, String targetType, Long targetId, String details) {
        this.admin = Objects.requireNonNull(admin, "admin");
        this.action = Objects.requireNonNull(action, "action");
        this.targetType = Objects.requireNonNull(targetType, "targetType");
        this.targetId = targetId;
        this.details = details;
    }

    public User getAdmin() {
        return admin;
    }

    public String getAction() {
        return action;
    }

    public String getTargetType() {
        return targetType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public String getDetails() {
        return details;
    }
}
