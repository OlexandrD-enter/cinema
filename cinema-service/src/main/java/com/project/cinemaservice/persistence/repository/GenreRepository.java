package com.project.cinemaservice.persistence.repository;

import com.project.cinemaservice.persistence.model.Genre;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Genre entities in the database.
 */
@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

  Optional<Genre> findByName(String name);
}
