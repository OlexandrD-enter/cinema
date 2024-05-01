package com.project.cinemaservice.api.controller;

import com.project.cinemaservice.domain.dto.movie.MovieClientResponse;
import com.project.cinemaservice.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller handling operations related to movies.<br> Endpoints provided:<br>
 * - GET /movieId: Gets a movie details for client.<br>
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
}
