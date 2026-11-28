package com.tutorbridge.tuition;

import com.tutorbridge.catalog.Area;
import com.tutorbridge.catalog.Subject;
import com.tutorbridge.common.BaseEntity;
import com.tutorbridge.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A guardian's tuition requirement. M:1 with the guardian {@link User} and the {@link Area};
 * M:M with the {@link Subject}s to be taught.
 */
@Entity
@Table(name = "tuition_posts")
public class TuitionPost extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guardian_id", nullable = false)
    private User guardian;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;

    @ManyToMany
    @JoinTable(name = "post_subjects",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id"))
    private Set<Subject> subjects = new HashSet<>();

    @Column(nullable = false, length = 120)
    private String title;

    @Column(name = "class_level", nullable = false, length = 30)
    private String classLevel;

    @Column(nullable = false, length = 30)
    private String medium;

    @Column(nullable = false)
    private int salary;

    @Column(name = "days_per_week", nullable = false)
    private int daysPerWeek;

    @Column(length = 1000)
    private String details;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PostStatus status = PostStatus.OPEN;

    protected TuitionPost() {
    }

    public TuitionPost(User guardian, Area area, Collection<Subject> subjects, String title,
                       String classLevel, String medium, int salary, int daysPerWeek, String details) {
        this.guardian = Objects.requireNonNull(guardian, "guardian");
        update(area, subjects, title, classLevel, medium, salary, daysPerWeek, details);
    }

    /** Replaces the editable fields. Whether editing is allowed is decided by the service. */
    public void update(Area area, Collection<Subject> subjects, String title, String classLevel,
                       String medium, int salary, int daysPerWeek, String details) {
        this.area = Objects.requireNonNull(area, "area");
        this.subjects.clear();
        this.subjects.addAll(subjects);
        this.title = Objects.requireNonNull(title, "title");
        this.classLevel = Objects.requireNonNull(classLevel, "classLevel");
        this.medium = Objects.requireNonNull(medium, "medium");
        this.salary = salary;
        this.daysPerWeek = daysPerWeek;
        this.details = details;
    }

    public void changeStatus(PostStatus next) {
        this.status = Objects.requireNonNull(next, "status");
    }

    public boolean isOwnedBy(Long userId) {
        return guardian.getId() != null && guardian.getId().equals(userId);
    }

    public User getGuardian() {
        return guardian;
    }

    public Area getArea() {
        return area;
    }

    public Set<Subject> getSubjects() {
        return Collections.unmodifiableSet(subjects);
    }

    public String getTitle() {
        return title;
    }

    public String getClassLevel() {
        return classLevel;
    }

    public String getMedium() {
        return medium;
    }

    public int getSalary() {
        return salary;
    }

    public int getDaysPerWeek() {
        return daysPerWeek;
    }

    public String getDetails() {
        return details;
    }

    public PostStatus getStatus() {
        return status;
    }
}
