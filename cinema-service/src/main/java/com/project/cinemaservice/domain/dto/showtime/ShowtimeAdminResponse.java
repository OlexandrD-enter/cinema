package com.project.cinemaservice.domain.dto.showtime;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowtimeAdminResponse {

  private Long id;
  private Long movieId;
  private Long cinemaRoomId;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String createdBy;
  private String modifiedBy;
}
