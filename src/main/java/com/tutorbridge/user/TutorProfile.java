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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A tutor's tuition profile (1:1 with {@link User}). Subjects and preferred areas are real
 * many-to-many relationships to the catalog lookup tables; class levels and mediums stay simple
 * string collections.
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

    @Column(name = "salary_min")
    private Integer salaryMin;

    @Column(name = "salary_max")
    private Integer salaryMax;

    @Column(nullable = false)
    private boolean verified;

    @Column(name = "rating_avg", nullable = false)
    private double ratingAvg;

    @Column(name = "rating_count", nullable = false)
    private int ratingCount;

    @ManyToMany
    @JoinTable(name = "tutor_subjects",
            joinColumns = @JoinColumn(name = "tutor_profile_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id"))
    private Set<Subject> subjects = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "tutor_areas",
            joinColumns = @JoinColumn(name = "tutor_profile_id"),
            inverseJoinColumns = @JoinColumn(name = "area_id"))
    private Set<Area> preferredAreas = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "tutor_class_levels", joinColumns = @JoinColumn(name = "tutor_profile_id"))
    @Column(name = "class_level", nullable = false, length = 30)
    private Set<String> classLevels = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "tutor_mediums", joinColumns = @JoinColumn(name = "tutor_profile_id"))
    @Column(name = "medium", nullable = false, length = 30)
    private Set<String> mediums = new HashSet<>();

    protected TutorProfile() {
    }

    public TutorProfile(User user) {
        this.user = Objects.requireNonNull(user, "user");
    }

    public void updateDetails(String institution, String bio, Integer salaryMin, Integer salaryMax) {
        this.institution = institution;
        this.bio = bio;
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
    }

    public void replaceSubjects(Collection<Subject> values) {
        subjects.clear();
        subjects.addAll(values);
    }

    public void replacePreferredAreas(Collection<Area> values) {
        preferredAreas.clear();
        preferredAreas.addAll(values);
    }

    public void replaceClassLevels(Collection<String> levels) {
        classLevels.clear();
        classLevels.addAll(levels);
    }

    public void replaceMediums(Collection<String> values) {
        mediums.clear();
        mediums.addAll(values);
    }

    public void verify() {
        this.verified = true;
    }

    public User getUser() {
        return user;
    }

    public String getInstitution() {
        return institution;
    }

    public String getBio() {
        return bio;
    }

    public Integer getSalaryMin() {
        return salaryMin;
    }

    public Integer getSalaryMax() {
        return salaryMax;
    }

    public boolean isVerified() {
        return verified;
    }

    public double getRatingAvg() {
        return ratingAvg;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public Set<Subject> getSubjects() {
        return Collections.unmodifiableSet(subjects);
    }

    public Set<Area> getPreferredAreas() {
        return Collections.unmodifiableSet(preferredAreas);
    }

    public Set<String> getClassLevels() {
        return Collections.unmodifiableSet(classLevels);
    }

    public Set<String> getMediums() {
        return Collections.unmodifiableSet(mediums);
    }
}
