package com.project.cinemaservice.service;

import com.project.cinemaservice.domain.dto.movie.MovieAdminResponse;
import com.project.cinemaservice.domain.dto.movie.MovieClientResponse;
import com.project.cinemaservice.domain.dto.movie.MovieDataRequest;
import com.project.cinemaservice.domain.dto.movie.MovieEditRequest;

/**
 * MovieService interface for managing Movie related operations.
 */
public interface MovieService {

  MovieAdminResponse createMovie(MovieDataRequest movieDataRequest);

  MovieAdminResponse editMovie(Long movieId, MovieEditRequest movieEditRequest);

  MovieAdminResponse getMovieForAdminById(Long movieId);
  MovieClientResponse getMovieForClientById(Long movieId);
}
