package com.project.cinemaservice.api.admin;

import com.project.cinemaservice.domain.dto.roomseat.RoomSeatAdminResponse;
import com.project.cinemaservice.domain.dto.roomseat.RoomSeatCreateRequest;
import com.project.cinemaservice.domain.dto.roomseat.RoomSeatEditRequest;
import com.project.cinemaservice.service.RoomSeatService;
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
 * Controller handling operations related to room seats.<br> Endpoints provided:<br>
 * - POST /: Creates a new room seat based on request data.<br>
 * - PUT /{roomSeatId}: Updates a room seat based on request data.<br>
 * - GET /roomSeatId: Gets a room seat for admin.<br>
 * - DELETE /{roomSeatId}: Deletes a room seat based on request data.<br>
 */
@Validated
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/admin/room-seats")
public class RoomSeatController {

  private final RoomSeatService roomSeatService;

  @Operation(summary = "This method is used for roomSeat creation.")
  @PostMapping
  public ResponseEntity<RoomSeatAdminResponse> createRoomSeat(
      @RequestBody @Valid RoomSeatCreateRequest roomSeatCreateRequest) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(roomSeatService.createRoomSeat(roomSeatCreateRequest));
  }

  @Operation(summary = "This method is used to edit the roomSeat.")
  @PutMapping("/{roomSeatId}")
  public ResponseEntity<RoomSeatAdminResponse> editRoomSeat(
      @PathVariable Long roomSeatId,
      @RequestBody @Valid RoomSeatEditRequest roomSeatEditRequest) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(roomSeatService.editRoomSeat(roomSeatId, roomSeatEditRequest));
  }

  @Operation(summary = "This method retrieves a roomSeat details.")
  @GetMapping("/{roomSeatId}")
  public ResponseEntity<RoomSeatAdminResponse> getRoomSeat(
      @PathVariable @Min(1) Long roomSeatId) {
    return ResponseEntity.ok(roomSeatService.getRoomSeatById(roomSeatId));
  }

  @Operation(summary = "This method is used to delete the roomSeat.")
  @DeleteMapping("/{roomSeatId}")
  public ResponseEntity<HttpStatus> deleteRoomSeat(@PathVariable @Min(1) Long roomSeatId) {
    roomSeatService.deleteRoomSeatById(roomSeatId);
    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
