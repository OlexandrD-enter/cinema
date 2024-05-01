package com.project.cinemaservice.persistence.repository;

import com.project.cinemaservice.persistence.model.Movie;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Movie entities in the database.
 */
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

  Optional<Movie> findByName(String movieName);

  boolean existsByNameAndIdNot(String movieName, Long movieId);
}
