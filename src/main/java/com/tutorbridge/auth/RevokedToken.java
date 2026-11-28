package com.tutorbridge.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.Instant;
import java.util.Objects;
import org.springframework.data.domain.Persistable;

/**
 * A logged-out token's {@code jti}, kept until the token would have expired anyway.
 * Implements {@link Persistable} so saving a new row is a plain insert (the id is assigned).
 */
@Entity
@Table(name = "revoked_tokens")
public class RevokedToken implements Persistable<String> {

    @Id
    @Column(length = 64)
    private String jti;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Transient
    private boolean isNew = true;

    protected RevokedToken() {
        this.isNew = false;
    }

    public RevokedToken(String jti, Instant expiresAt) {
        this.jti = Objects.requireNonNull(jti, "jti");
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt");
    }

    @Override
    public String getId() {
        return jti;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public String getJti() {
        return jti;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
