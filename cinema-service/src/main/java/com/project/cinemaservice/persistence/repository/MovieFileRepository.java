package com.project.cinemaservice.persistence.repository;

import com.project.cinemaservice.persistence.enums.MovieFileType;
import com.project.cinemaservice.persistence.model.Movie;
import com.project.cinemaservice.persistence.model.MovieFile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing MovieFile entities in the database.
 */
@Repository
public interface MovieFileRepository extends JpaRepository<MovieFile, Long> {

  Optional<MovieFile> findByMovieAndMovieFileType(Movie movie, MovieFileType movieFileType);
}
