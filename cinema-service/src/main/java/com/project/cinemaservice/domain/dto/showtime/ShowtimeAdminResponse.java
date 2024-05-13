package com.project.cinemaservice.domain.dto.showtime;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a showtime admin response.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowtimeAdminResponse extends ShowtimeClientResponse {

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String createdBy;
  private String modifiedBy;
}
