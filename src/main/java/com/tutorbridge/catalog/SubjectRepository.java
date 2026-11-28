package com.tutorbridge.catalog;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Optional<Subject> findByName(String name);

    List<Subject> findByNameIn(Collection<String> names);

    List<Subject> findAllByOrderByNameAsc();
}
