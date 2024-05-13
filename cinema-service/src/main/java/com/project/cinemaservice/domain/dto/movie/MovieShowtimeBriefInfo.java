package com.project.cinemaservice.domain.dto.movie;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class MovieShowtimeBriefInfo {

  private Long id;
  private LocalDateTime startDate;
}
