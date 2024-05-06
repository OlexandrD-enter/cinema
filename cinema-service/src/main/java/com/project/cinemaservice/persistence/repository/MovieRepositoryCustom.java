package com.project.cinemaservice.persistence.repository;

import com.project.cinemaservice.domain.dto.movie.MovieFilters;
import com.project.cinemaservice.domain.dto.movie.MoviePageDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for advanced querying Movie entities.
 */
@Repository
public interface MovieRepositoryCustom {

  Page<MoviePageDetails> findAllByFilters(Pageable pageable,
      MovieFilters movieFilters);
}
