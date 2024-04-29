package com.project.cinemaservice.domain.dto.genre;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a genre admin brief response.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenreAdminBriefResponse {

  private Long id;
  private String name;
}
