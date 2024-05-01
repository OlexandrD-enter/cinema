package com.project.cinemaservice.domain.dto.movie;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a response for movie file information with link.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieFileResponseUrl extends MovieFileResponse {

  private String accessUrl;
}
