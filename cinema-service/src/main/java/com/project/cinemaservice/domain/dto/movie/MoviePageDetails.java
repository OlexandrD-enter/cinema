package com.project.cinemaservice.domain.dto.movie;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a movie details response from db.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class MoviePageDetails {

  private Long id;

  private String name;

  private Integer ageLimit;

  private String language;

  private String country;

  private LocalDateTime realiseDate;

  private Long fileId;

  private List<String> movieGenres;
}
