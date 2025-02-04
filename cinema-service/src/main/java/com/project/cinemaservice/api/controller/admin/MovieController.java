package com.project.cinemaservice.api.controller.admin;

import com.project.cinemaservice.domain.dto.movie.MovieAdminResponse;
import com.project.cinemaservice.domain.dto.movie.MovieBriefInfo;
import com.project.cinemaservice.domain.dto.movie.MovieDataRequest;
import com.project.cinemaservice.domain.dto.movie.MovieEditRequest;
import com.project.cinemaservice.domain.dto.movie.MovieFiltersRequest;
import com.project.cinemaservice.domain.dto.movie.MoviePageDetailsAdminResponse;
import com.project.cinemaservice.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller handling operations related to movies.<br> Endpoints provided:<br>
 * - POST /: Creates a new movie based on request data.<br>
 * - PUT /{movieId}: Edit a movie based on request data.<br>
 * - GET /{movieId}: Gets a movie details for admin.<br>
 * - DELETE /{movieId}: Deletes a movie based on request data.<br>
 * - PUT /{movieId}/status: Change movie status based on request data.<br>
 * - GET /filters: Get a movies information base on filters.<br>
 */
@Validated
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/admin/movies")
public class MovieController {

  private final MovieService movieService;

  @Operation(summary = "This method is used for movie creation.")
  @PostMapping
  public ResponseEntity<MovieAdminResponse> createMovie(
      @ModelAttribute @Valid MovieDataRequest movieDataRequest) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(movieService.createMovie(movieDataRequest));
  }

  @Operation(summary = "This method is used for movie update.")
  @PutMapping("/{movieId}")
  public ResponseEntity<MovieAdminResponse> editMovie(
      @PathVariable Long movieId,
      @ModelAttribute @Valid MovieEditRequest movieEditRequest) {
    return ResponseEntity.ok().body(movieService.editMovie(movieId, movieEditRequest));
  }

  @Operation(summary = "This method retrieves a movie details.")
  @GetMapping("/{movieId}")
  public ResponseEntity<MovieAdminResponse> getMovie(
      @PathVariable @Min(1) Long movieId) {
    return ResponseEntity.ok().body(movieService.getMovieForAdminById(movieId));
  }

  @Operation(summary = "This method is used to delete the movies.")
  @DeleteMapping("/{movieId}")
  public ResponseEntity<HttpStatus> deleteMovie(@PathVariable @Min(1) Long movieId) {
    movieService.deleteMovieById(movieId);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @Operation(summary = "This method is used to change movie status.")
  @PutMapping("/{movieId}/status")
  public ResponseEntity<MovieBriefInfo> changeMoviePublishStatus(
      @PathVariable @Min(1) Long movieId,
      @RequestParam(value = "is_published") boolean isPublished) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(movieService.changeMovieStatus(movieId, isPublished));
  }

  @Operation(summary = "This method is used to get all movies by filters.")
  @GetMapping("/filters")
  public ResponseEntity<Page<MoviePageDetailsAdminResponse>> getAllMovies(
      Pageable pageable,
      @RequestBody @Valid MovieFiltersRequest movieFiltersRequest) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(movieService.getAllMoviesByFiltersForAdmin(pageable, movieFiltersRequest));
  }
}
