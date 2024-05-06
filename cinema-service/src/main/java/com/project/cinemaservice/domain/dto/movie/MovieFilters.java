package com.project.cinemaservice.domain.dto.movie;

import com.project.cinemaservice.persistence.model.Genre;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents filters for querying movies.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class MovieFilters {

  private List<Genre> genres;
  private String country;
  private String language;
  private Integer minAge;
  private Integer maxAge;
}
