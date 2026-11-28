package com.tutorbridge.tuition;

import com.tutorbridge.user.UserStatus;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TuitionPostRepository extends JpaRepository<TuitionPost, Long>, JpaSpecificationExecutor<TuitionPost> {

    List<TuitionPost> findByGuardianIdOrderByCreatedAtDesc(Long guardianId);

    Optional<TuitionPost> findByIdAndGuardianId(Long id, Long guardianId);

    List<TuitionPost> findByStatusInAndGuardianStatus(Collection<PostStatus> statuses, UserStatus guardianStatus);

    long countByStatus(PostStatus status);
}
