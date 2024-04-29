package com.project.cinemaservice.domain.dto.genre;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a genre admin response.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenreAdminResponse extends GenreAdminBriefResponse {

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String createdBy;
  private String modifiedBy;
}
