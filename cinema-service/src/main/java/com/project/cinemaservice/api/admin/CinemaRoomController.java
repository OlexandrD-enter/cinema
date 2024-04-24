package com.project.cinemaservice.api.admin;

import com.project.cinemaservice.domain.dto.cinemaroom.CinemaRoomAdminResponse;
import com.project.cinemaservice.domain.dto.cinemaroom.CinemaRoomCreateRequest;
import com.project.cinemaservice.domain.dto.cinemaroom.CinemaRoomEditRequest;
import com.project.cinemaservice.service.CinemaRoomService;
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
 * Controller handling operations related to cinema rooms.<br>
 * Endpoints provided:<br>
 * - POST /: Creates a new cinema room based on request data.<br>
 * - PUT /{cinemaRoomId}: Updates a cinema room based on request data.<br>
 * - GET /: Gets a cinema room for admin.<br>
 * - DELETE /{cinemaRoomId}: Deletes a cinema room based on request data.<br>
 */
@Validated
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/admin/cinema-rooms")
public class CinemaRoomController {

  private final CinemaRoomService cinemaRoomService;

  @Operation(summary = "This method is used for cinemaRoom creation.")
  @PostMapping
  public ResponseEntity<CinemaRoomAdminResponse> createCinemaRoom(
      @RequestBody @Valid CinemaRoomCreateRequest cinemaRoomCreateRequest) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(cinemaRoomService.createCinemaRoom(cinemaRoomCreateRequest));
  }

  @Operation(summary = "This method is used to edit the cinemaRoom.")
  @PutMapping("/{cinemaRoomId}")
  public ResponseEntity<CinemaRoomAdminResponse> editCinemaRoom(
      @PathVariable Long cinemaRoomId,
      @RequestBody @Valid CinemaRoomEditRequest cinemaRoomEditRequest) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(cinemaRoomService.editCinemaRoom(cinemaRoomId, cinemaRoomEditRequest));
  }

  @Operation(summary = "This method retrieves a cinemaRoom details.")
  @GetMapping("/{cinemaRoomId}")
  public ResponseEntity<CinemaRoomAdminResponse> getCinemaRoom(
      @PathVariable @Min(1) Long cinemaRoomId) {
    return ResponseEntity.ok(cinemaRoomService.getCinemaRoomById(cinemaRoomId));
  }

  @Operation(summary = "This method is used to delete the cinemaRoom.")
  @DeleteMapping("/{cinemaRoomId}")
  public ResponseEntity<HttpStatus> deleteCinemaRoom(@PathVariable @Min(1) Long cinemaRoomId) {
    cinemaRoomService.deleteCinemaRoomById(cinemaRoomId);
    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
