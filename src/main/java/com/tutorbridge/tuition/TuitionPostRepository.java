package com.tutorbridge.tuition;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TuitionPostRepository
        extends JpaRepository<TuitionPost, Long>, JpaSpecificationExecutor<TuitionPost> {

    List<TuitionPost> findByGuardianIdOrderByCreatedAtDesc(Long guardianId);

    Optional<TuitionPost> findByIdAndGuardianId(Long id, Long guardianId);

    List<TuitionPost> findByStatusInOrderByCreatedAtDesc(List<PostStatus> statuses);

    long countByStatus(PostStatus status);
}
