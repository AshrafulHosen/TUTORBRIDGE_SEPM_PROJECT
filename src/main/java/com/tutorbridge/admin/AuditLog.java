package com.tutorbridge.admin;

import com.tutorbridge.common.BaseEntity;
import com.tutorbridge.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Immutable audit trail entry recording administrative moderation actions.
 */
@Entity
@Table(name = "audit_logs")
public class AuditLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    @Column(nullable = false, length = 80)
    private String action;

    @Column(name = "target_type", nullable = false, length = 80)
    private String targetType;

    @Column(name = "target_id")
    private Long targetId;

    @Column(length = 1000)
    private String details;

    protected AuditLog() {
    }

    public AuditLog(User admin, String action, String targetType, Long targetId, String details) {
        this.admin = admin;
        this.action = action;
        this.targetType = targetType;
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
