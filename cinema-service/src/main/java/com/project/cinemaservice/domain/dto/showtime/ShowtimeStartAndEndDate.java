package com.project.cinemaservice.domain.dto.showtime;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a showtime dto which contains start and end date for showtime of movie.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowtimeStartAndEndDate {

  private Long id;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
}
