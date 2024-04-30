package com.project.cinemaservice.domain.dto.movie;


import com.project.cinemaservice.persistence.enums.MovieFileType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a response for movie file information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieFileResponse {

  private Long id;
  private String name;
  private Long targetId;
  private MovieFileType targetType;
}
