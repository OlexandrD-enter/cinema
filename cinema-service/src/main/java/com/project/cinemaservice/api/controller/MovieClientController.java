package com.project.cinemaservice.api.controller;

import com.project.cinemaservice.domain.dto.movie.MovieClientResponse;
import com.project.cinemaservice.domain.dto.movie.MovieFiltersRequest;
import com.project.cinemaservice.domain.dto.movie.MoviePageDetailsResponse;
import com.project.cinemaservice.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller handling operations related to movies.<br> Endpoints provided:<br>
 * - GET /movieId: Gets a movie details for client.<br>
 * - GET /filters: Get a movies information base on filters.<br>
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movies")
public class MovieClientController {

  private final MovieService movieService;

  @Operation(summary = "This method retrieves a movie details.")
  @GetMapping("/{movieId}")
  public ResponseEntity<MovieClientResponse> getMovie(
      @PathVariable @Min(1) Long movieId) {
    return ResponseEntity.ok().body(movieService.getMovieForClientById(movieId));
  }

  @Operation(summary = "This method is used to get all movies by filters.")
  @GetMapping("/filters")
  public ResponseEntity<Page<MoviePageDetailsResponse>> getAllMovies(
      Pageable pageable,
      @RequestBody @Valid MovieFiltersRequest movieFiltersRequest) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(movieService.getAllMoviesByFilter(pageable, movieFiltersRequest));
  }
}
