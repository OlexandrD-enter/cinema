package com.project.cinemaservice.domain.dto.cinemaroom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a cinema brief response.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CinemaRoomBriefInfo {
  private Long id;
  private String name;
  private String roomType;
}
