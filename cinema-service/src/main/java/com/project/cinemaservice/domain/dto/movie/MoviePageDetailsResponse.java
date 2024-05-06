package com.project.cinemaservice.domain.dto.movie;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a movie details response.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class MoviePageDetailsResponse {

  private Long id;

  private String name;

  private Integer ageLimit;

  private String language;

  private String country;

  private LocalDateTime realiseDate;

  private List<String> movieGenres;

  private String previewUrl;
}
