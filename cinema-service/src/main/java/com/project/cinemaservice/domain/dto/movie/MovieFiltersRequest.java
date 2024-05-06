package com.project.cinemaservice.domain.dto.movie;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents filter request for querying movies.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class MovieFiltersRequest {

  private List<Long> genreIds;
  private String country;
  private String language;
  private Integer minAge;
  private Integer maxAge;
}
