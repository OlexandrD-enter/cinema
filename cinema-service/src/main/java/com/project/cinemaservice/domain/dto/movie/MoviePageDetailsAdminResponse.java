package com.project.cinemaservice.domain.dto.movie;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a movie details admin response.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MoviePageDetailsAdminResponse extends MoviePageDetailsResponse {

  private Boolean isPublish;
}
