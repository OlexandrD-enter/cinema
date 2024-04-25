package com.project.cinemaservice.domain.dto.roomseat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a room seat  response for admin.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomSeatAdminResponse {

  private Long id;
  private Long seatNumber;
  private Long cinemaRoomId;
}
