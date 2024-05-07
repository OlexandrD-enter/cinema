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
public class ShowtimeDataRequest {
  private Long movieId;
  private Long cinemaRoomId;
  private LocalDateTime startDate;
}
