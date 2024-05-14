package com.project.cinemaservice.service;

import com.project.cinemaservice.domain.dto.movie.MovieAdminResponse;
import com.project.cinemaservice.domain.dto.movie.MovieBriefInfo;
import com.project.cinemaservice.domain.dto.movie.MovieClientResponse;
import com.project.cinemaservice.domain.dto.movie.MovieDataRequest;
import com.project.cinemaservice.domain.dto.movie.MovieEditRequest;
import com.project.cinemaservice.domain.dto.movie.MovieFiltersRequest;
import com.project.cinemaservice.domain.dto.movie.MoviePageDetailsAdminResponse;
import com.project.cinemaservice.domain.dto.movie.MoviePageDetailsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * MovieService interface for managing Movie related operations.
 */
public interface MovieService {

  MovieAdminResponse createMovie(MovieDataRequest movieDataRequest);

  MovieAdminResponse editMovie(Long movieId, MovieEditRequest movieEditRequest);

  MovieAdminResponse getMovieForAdminById(Long movieId);

  MovieClientResponse getMovieForClientById(Long movieId);

  void deleteMovieById(Long movieId);

  Page<MoviePageDetailsResponse> getAllMoviesByFiltersForClient(Pageable pageable,
      MovieFiltersRequest movieFiltersRequest);

  Page<MoviePageDetailsAdminResponse> getAllMoviesByFiltersForAdmin(Pageable pageable,
      MovieFiltersRequest movieFiltersRequest);

  MovieBriefInfo changeMovieStatus(Long movieId, boolean movieStatus);
}
