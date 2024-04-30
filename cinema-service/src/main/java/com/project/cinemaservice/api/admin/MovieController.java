package com.project.cinemaservice.api.admin;

import com.project.cinemaservice.domain.dto.movie.MovieAdminResponse;
import com.project.cinemaservice.domain.dto.movie.MovieCreateRequest;
import com.project.cinemaservice.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller handling operations related to movies.<br> Endpoints provided:<br>
 * - POST /: Creates a new movie based on request data.<br>
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
      @ModelAttribute @Valid MovieCreateRequest movieCreateRequest) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(movieService.createMovie(movieCreateRequest));
  }
}
