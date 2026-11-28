package com.tutorbridge.demo;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemoSessionRepository extends JpaRepository<DemoSession, Long> {

    Optional<DemoSession> findByApplicationId(Long applicationId);

    List<DemoSession> findByApplicationPostGuardianIdOrApplicationTutorIdOrderByScheduledAtAsc(
            Long guardianId, Long tutorId);
}
