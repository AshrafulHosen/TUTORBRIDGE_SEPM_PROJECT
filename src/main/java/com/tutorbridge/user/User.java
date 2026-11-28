package com.tutorbridge.user;

import com.tutorbridge.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.Locale;
import java.util.Objects;

/**
 * A registered account. Every user has exactly one role; tutors and guardians also have a
 * one-to-one profile ({@link TutorProfile}, {@link GuardianProfile}).
 */
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(nullable = false, length = 254)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status = UserStatus.ACTIVE;

    protected User() {
    }

    public User(String email, String passwordHash, String fullName, String phone, Role role) {
        this.email = normaliseEmail(email);
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash");
        this.fullName = Objects.requireNonNull(fullName, "fullName");
        this.phone = phone;
        this.role = Objects.requireNonNull(role, "role");
    }

    private static String normaliseEmail(String email) {
        return Objects.requireNonNull(email, "email").trim().toLowerCase(Locale.ROOT);
    }

    public void updateContact(String fullName, String phone) {
        this.fullName = Objects.requireNonNull(fullName, "fullName");
        this.phone = phone;
    }

    public void suspend() {
        this.status = UserStatus.SUSPENDED;
    }

    public void restore() {
        this.status = UserStatus.ACTIVE;
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public Role getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }
}
