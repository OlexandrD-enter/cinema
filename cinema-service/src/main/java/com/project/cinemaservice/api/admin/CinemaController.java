package com.project.cinemaservice.api.admin;

import com.project.cinemaservice.domain.dto.cinema.CinemaAdminResponse;
import com.project.cinemaservice.domain.dto.cinema.CinemaDataRequest;
import com.project.cinemaservice.service.CinemaService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller handling operations related to cinemas.<br>
 * Endpoints provided:<br>
 * - POST /: Creates a new cinema based on request data.<br>
 * - PUT /{cinemaId}: Updates a cinema based on request data.<br>
 * - GET /{cinemaId}: Gets a cinema for admin.<br>
 * - DELETE /{cinemaId}: Deletes a cinema based on request data.<br>
 */
@Validated
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/admin/cinemas")
public class CinemaController {

  private final CinemaService cinemaService;

  @Operation(summary = "This method is used for cinema creation.")
  @PostMapping
  public ResponseEntity<CinemaAdminResponse> createCinema(
      @RequestBody @Valid CinemaDataRequest cinemaDataRequest) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(cinemaService.createCinema(cinemaDataRequest));
  }

  @Operation(summary = "This method is used to edit the cinema.")
  @PutMapping("/{cinemaId}")
  public ResponseEntity<CinemaAdminResponse> editCinema(
      @PathVariable Long cinemaId,
      @RequestBody @Valid CinemaDataRequest cinemaDataRequest) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(cinemaService.editCinema(cinemaId, cinemaDataRequest));
  }

  @Operation(summary = "This method retrieves a cinema details.")
  @GetMapping("/{cinemaId}")
  public ResponseEntity<CinemaAdminResponse> getCinema(@PathVariable @Min(1) Long cinemaId) {
    return ResponseEntity.ok(cinemaService.getCinemaById(cinemaId));
  }

  @Operation(summary = "This method is used to delete the cinema.")
  @DeleteMapping("/{cinemaId}")
  public ResponseEntity<HttpStatus> deleteCinema(@PathVariable @Min(1) Long cinemaId) {
    cinemaService.deleteCinemaById(cinemaId);
    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
