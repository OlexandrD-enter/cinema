package com.project.cinemaservice.service;

import com.project.cinemaservice.domain.dto.movie.MovieAdminResponse;
import com.project.cinemaservice.domain.dto.movie.MovieCreateRequest;

/**
 * MovieService interface for managing Movie related operations.
 */
public interface MovieService {

  MovieAdminResponse createMovie(MovieCreateRequest movieCreateRequest);
}
