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

  /**
   * Constructor with builder.
   *
   * @param id         The genre ID.
   * @param name       The genre name.
   * @param createdAt  The date and time when the genre was created.
   * @param updatedAt  The date and time when the genre was last updated.
   * @param createdBy  The email of the user who created the genre.
   * @param modifiedBy The email of the user who last modified the genre.
   */
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
