package com.project.cinemaservice.domain.dto.roomseat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a room seat brief response.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomSeatBriefInfo {

  private Long id;
  private Long seatNumber;
}
