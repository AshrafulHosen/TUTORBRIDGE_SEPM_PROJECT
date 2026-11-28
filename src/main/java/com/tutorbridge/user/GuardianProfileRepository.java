package com.tutorbridge.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuardianProfileRepository extends JpaRepository<GuardianProfile, Long> {

    Optional<GuardianProfile> findByUserId(Long userId);
}
