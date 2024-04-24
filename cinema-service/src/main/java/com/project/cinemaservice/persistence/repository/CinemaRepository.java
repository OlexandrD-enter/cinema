package com.project.cinemaservice.persistence.repository;

import com.project.cinemaservice.persistence.model.Cinema;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Cinema entities in the database.
 */
@Repository
public interface CinemaRepository extends JpaRepository<Cinema, Long> {

  Optional<Cinema> findByName(String name);

  @EntityGraph(attributePaths = {"cinemaRooms"})
  Optional<Cinema> findById(Long id);
}
