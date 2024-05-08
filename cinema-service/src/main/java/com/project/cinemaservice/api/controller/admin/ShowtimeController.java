package com.project.cinemaservice.api.controller.admin;

import com.project.cinemaservice.domain.dto.showtime.ShowtimeAdminResponse;
import com.project.cinemaservice.domain.dto.showtime.ShowtimeDataRequest;
import com.project.cinemaservice.service.ShowtimeService;
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
 * Controller handling operations related to showtime.<br> Endpoints provided:<br>
 * - POST /: Creates a new showtime based on request data.<br>
 * - PUT /{showtimeId}: Updates a showtime based on request data.<br>
 * - GET /showtimeId: Gets a showtime for admin.<br>
 * - DELETE /{showtimeId}: Deletes a showtime based on request data.<br>
 */
@Validated
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/admin/showtimes")
public class ShowtimeController {
  private final ShowtimeService showtimeService;

  @Operation(summary = "This method is used for showtime creation.")
  @PostMapping
  public ResponseEntity<ShowtimeAdminResponse> createShowtime(
      @RequestBody @Valid ShowtimeDataRequest showtimeDataRequest) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(showtimeService.createShowtime(showtimeDataRequest));
  }

  @Operation(summary = "This method is used to edit the showtime.")
  @PutMapping("/{showtimeId}")
  public ResponseEntity<ShowtimeAdminResponse> editShowtime(
      @PathVariable Long showtimeId,
      @RequestBody @Valid ShowtimeDataRequest showtimeDataRequest) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(showtimeService.editShowtime(showtimeId, showtimeDataRequest));
  }

  @Operation(summary = "This method retrieves a showtime details.")
  @GetMapping("/{showtimeId}")
  public ResponseEntity<ShowtimeAdminResponse> getShowtime(
      @PathVariable @Min(1) Long showtimeId) {
    return ResponseEntity.ok(showtimeService.getShowtimeById(showtimeId));
  }

  @Operation(summary = "This method is used to delete the showtime.")
  @DeleteMapping("/{showtimeId}")
  public ResponseEntity<HttpStatus> deleteShowtime(@PathVariable @Min(1) Long showtimeId) {
    showtimeService.deleteShowtimeById(showtimeId);
    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
