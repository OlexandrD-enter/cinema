package com.project.cinemaservice.domain.dto.movie;

import com.project.cinemaservice.persistence.enums.MovieFileType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a request for movie file information.
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieFileRequest {

  private Long targetId;
  private MovieFileType targetType;
  private String mimeType;
}
