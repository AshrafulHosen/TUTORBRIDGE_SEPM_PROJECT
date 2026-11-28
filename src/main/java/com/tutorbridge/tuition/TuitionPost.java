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
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Guardian tuition request specifying subjects, area, salary, and teaching schedule.
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
    @JoinTable(
            name = "post_subjects",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id"))
    private Set<Subject> subjects = new LinkedHashSet<>();

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "class_level", nullable = false, length = 50)
    private String classLevel;

    @Column(nullable = false, length = 50)
    private String medium;

    @Column(nullable = false)
    private int salary;

    @Column(name = "days_per_week", nullable = false)
    private int daysPerWeek;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PostStatus status = PostStatus.OPEN;

    protected TuitionPost() {
    }

    public TuitionPost(
            User guardian,
            Area area,
            Set<Subject> subjects,
            String title,
            String classLevel,
            String medium,
            int salary,
            int daysPerWeek,
            String description) {
        this.guardian = guardian;
        this.area = area;
        this.subjects = subjects == null ? new LinkedHashSet<>() : new LinkedHashSet<>(subjects);
        this.title = title;
        this.classLevel = classLevel;
        this.medium = medium;
        this.salary = salary;
        this.daysPerWeek = daysPerWeek;
        this.description = description;
        this.status = PostStatus.OPEN;
    }

    public User getGuardian() {
        return guardian;
    }

    public void setGuardian(User guardian) {
        this.guardian = guardian;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects == null ? new LinkedHashSet<>() : new LinkedHashSet<>(subjects);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getClassLevel() {
        return classLevel;
    }

    public void setClassLevel(String classLevel) {
        this.classLevel = classLevel;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public int getDaysPerWeek() {
        return daysPerWeek;
    }

    public void setDaysPerWeek(int daysPerWeek) {
        this.daysPerWeek = daysPerWeek;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PostStatus getStatus() {
        return status;
    }

    public void setStatus(PostStatus status) {
        this.status = status;
    }
}
