package com.project.cinemaservice.api.controller;

import com.project.cinemaservice.domain.dto.showtime.ShowtimeClientResponse;
import com.project.cinemaservice.service.ShowtimeService;
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
 * Controller handling operations related to showtimes.<br> Endpoints provided:<br>
 * - GET /{showtimeId}: Retrieves information about showtime.<br>
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/showtimes")
public class ShowtimeClientController {

  private final ShowtimeService showtimeService;

  @Operation(summary = "This method retrieves a showtime details for client.")
  @GetMapping("/{showtimeId}")
  public ResponseEntity<ShowtimeClientResponse> getShowtime(
      @PathVariable @Min(1) Long showtimeId) {
    return ResponseEntity.ok(showtimeService.getShowtimeByIdForClient(showtimeId));
  }
}
