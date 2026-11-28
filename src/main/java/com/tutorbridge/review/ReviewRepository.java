package com.tutorbridge.review;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByPostId(Long postId);

    Optional<Review> findByPostId(Long postId);

    List<Review> findByTutorIdOrderByCreatedAtDesc(Long tutorId);
}
