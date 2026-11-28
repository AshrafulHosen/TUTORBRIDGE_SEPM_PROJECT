package com.tutorbridge.user;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TutorProfileRepository extends JpaRepository<TutorProfile, Long> {

    Optional<TutorProfile> findByUserId(Long userId);

    List<TutorProfile> findByUserStatus(UserStatus status);

    long countByVerifiedFalse();
}
