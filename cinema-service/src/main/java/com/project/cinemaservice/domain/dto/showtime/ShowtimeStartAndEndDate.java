package com.project.cinemaservice.domain.dto.showtime;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowtimeStartAndEndDate {

  private Long id;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
}
