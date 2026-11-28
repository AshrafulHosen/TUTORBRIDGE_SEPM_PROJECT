package com.tutorbridge.user;

import com.tutorbridge.catalog.Area;
import com.tutorbridge.catalog.Subject;
import com.tutorbridge.common.BaseEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Tutor-specific profile holding teaching preferences, verification state, and rating summary.
 */
@Entity
@Table(name = "tutor_profiles")
public class TutorProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 150)
    private String institution;

    @Column(length = 1000)
    private String bio;

    @ManyToMany
    @JoinTable(
            name = "tutor_subjects",
            joinColumns = @JoinColumn(name = "tutor_profile_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id"))
    private Set<Subject> subjects = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(
            name = "tutor_areas",
            joinColumns = @JoinColumn(name = "tutor_profile_id"),
            inverseJoinColumns = @JoinColumn(name = "area_id"))
    private Set<Area> preferredAreas = new LinkedHashSet<>();

    @ElementCollection
    @CollectionTable(name = "tutor_class_levels", joinColumns = @JoinColumn(name = "tutor_profile_id"))
    @Column(name = "class_level", nullable = false, length = 50)
    private Set<String> classLevels = new LinkedHashSet<>();

    @ElementCollection
    @CollectionTable(name = "tutor_mediums", joinColumns = @JoinColumn(name = "tutor_profile_id"))
    @Column(name = "medium", nullable = false, length = 50)
    private Set<String> mediums = new LinkedHashSet<>();

    @Column(name = "salary_min")
    private Integer salaryMin;

    @Column(name = "salary_max")
    private Integer salaryMax;

    @Column(nullable = false)
    private boolean verified = false;

    @Column(name = "rating_avg", nullable = false)
    private double ratingAvg = 0.0;

    @Column(name = "rating_count", nullable = false)
    private int ratingCount = 0;

    protected TutorProfile() {
    }

    public TutorProfile(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects == null ? new LinkedHashSet<>() : new LinkedHashSet<>(subjects);
    }

    public Set<Area> getPreferredAreas() {
        return preferredAreas;
    }

    public void setPreferredAreas(Set<Area> preferredAreas) {
        this.preferredAreas = preferredAreas == null ? new LinkedHashSet<>() : new LinkedHashSet<>(preferredAreas);
    }

    public Set<String> getClassLevels() {
        return classLevels;
    }

    public void setClassLevels(Set<String> classLevels) {
        this.classLevels = classLevels == null ? new LinkedHashSet<>() : new LinkedHashSet<>(classLevels);
    }

    public Set<String> getMediums() {
        return mediums;
    }

    public void setMediums(Set<String> mediums) {
        this.mediums = mediums == null ? new LinkedHashSet<>() : new LinkedHashSet<>(mediums);
    }

    public Integer getSalaryMin() {
        return salaryMin;
    }

    public void setSalaryMin(Integer salaryMin) {
        this.salaryMin = salaryMin;
    }

    public Integer getSalaryMax() {
        return salaryMax;
    }

    public void setSalaryMax(Integer salaryMax) {
        this.salaryMax = salaryMax;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public double getRatingAvg() {
        return ratingAvg;
    }

    public void setRatingAvg(double ratingAvg) {
        this.ratingAvg = ratingAvg;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }
}
