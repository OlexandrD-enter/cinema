package com.project.cinemaservice.domain.dto.movie;

import java.time.LocalDateTime;
import java.util.List;
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
public class MovieAdminResponse {

  private Long id;
  private String name;
  private String description;
  private Integer ageLimit;
  private String language;
  private String country;
  private String director;
  private LocalDateTime realiseDate;
  private List<String> movieGenres;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String createdBy;
  private String modifiedBy;
}
