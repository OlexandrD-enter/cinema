package com.project.cinemaservice.persistence.repository;

import com.project.cinemaservice.persistence.model.MovieGenre;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing MovieGenre entities in the database.
 */
@Repository
public interface MovieGenreRepository extends JpaRepository<MovieGenre, Long> {

  @Modifying
  @Query("DELETE FROM MovieGenre mg WHERE mg.genre.id IN (:genreIds)")
  void deleteAllByGenreIds(List<Long> genreIds);
}
