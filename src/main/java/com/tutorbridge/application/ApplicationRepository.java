package com.tutorbridge.application;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    boolean existsByPostIdAndTutorId(Long postId, Long tutorId);

    boolean existsByPostId(Long postId);

    List<Application> findByPostIdOrderByMatchScoreDesc(Long postId);

    List<Application> findByPostIdAndStatusOrderByMatchScoreDesc(Long postId, ApplicationStatus status);

    List<Application> findByTutorIdOrderByCreatedAtDesc(Long tutorId);

    Optional<Application> findByPostIdAndStatus(Long postId, ApplicationStatus status);

    long countByPostId(Long postId);

    long countByPostIdAndStatus(Long postId, ApplicationStatus status);

    long countByCreatedAtAfter(Instant since);
}
