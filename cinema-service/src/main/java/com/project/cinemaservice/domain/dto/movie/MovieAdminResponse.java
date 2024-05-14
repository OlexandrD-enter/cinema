package com.project.cinemaservice.domain.dto.movie;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a movie admin response.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieAdminResponse extends MovieClientResponse {

  private Boolean isPublish;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String createdBy;
  private String modifiedBy;
}
