package com.tutorbridge.user;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TutorProfileRepository extends JpaRepository<TutorProfile, Long> {

    Optional<TutorProfile> findByUserId(Long userId);

    /** Candidate tutors for matching: profiles of users with the given status, with their catalog sets. */
    @EntityGraph(attributePaths = {"user", "subjects", "preferredAreas"})
    List<TutorProfile> findByUserStatus(UserStatus status);

    List<TutorProfile> findByUserIdIn(Collection<Long> userIds);

    long countByVerifiedFalse();
}
