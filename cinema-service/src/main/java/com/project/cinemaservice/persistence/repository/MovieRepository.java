package com.project.cinemaservice.persistence.repository;

import com.project.cinemaservice.persistence.model.Movie;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Movie entities in the database.
 */
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>, MovieRepositoryCustom {

  Optional<Movie> findByName(String movieName);

  @EntityGraph(attributePaths = {"movieGenres"})
  Optional<Movie> findById(Long movieId);

  boolean existsByNameAndIdNot(String movieName, Long movieId);
}
