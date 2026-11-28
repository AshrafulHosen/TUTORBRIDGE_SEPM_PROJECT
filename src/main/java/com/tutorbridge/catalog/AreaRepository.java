package com.tutorbridge.catalog;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AreaRepository extends JpaRepository<Area, Long> {

    Optional<Area> findByName(String name);

    List<Area> findByNameIn(Collection<String> names);

    List<Area> findAllByOrderByNameAsc();
}
