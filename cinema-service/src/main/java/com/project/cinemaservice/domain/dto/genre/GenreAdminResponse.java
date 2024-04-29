package com.project.cinemaservice.domain.dto.genre;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
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

  @Builder(builderMethodName = "builder")
  public GenreAdminResponse(Long id, String name, LocalDateTime createdAt, LocalDateTime updatedAt,
      String createdBy, String modifiedBy) {
    super(id, name);
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.createdBy = createdBy;
    this.modifiedBy = modifiedBy;
  }
}
