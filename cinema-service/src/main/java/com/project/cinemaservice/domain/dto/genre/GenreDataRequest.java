package com.project.cinemaservice.domain.dto.genre;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a genre create/update request.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenreDataRequest {

  private String name;
}
