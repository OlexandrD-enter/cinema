package com.project.cinemaservice.domain.dto.genre;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

  @Schema(example = "Drama")
  @NotNull(message = "Not valid name")
  @Pattern(regexp = "^[a-zA-Z ]{2,25}$",
      message = "Not valid name, only letters, length [2,25]")
  private String name;
}
