package com.project.cinemaservice.domain.dto.roomseat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a request for creat room seat.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomSeatCreateRequest {

  @Schema(example = "1")
  @NotNull(message = "Not valid seat number")
  @Min(value = 1, message = "Seat number must be greater than 0")
  private Long seatNumber;
  @Schema(example = "1")
  @NotNull(message = "Not valid cinemaRoomId")
  @Min(value = 1, message = "CinemaRoomId must be greater than 0")
  private Long cinemaRoomId;
}
