package com.project.cinemaservice.domain.dto.cinema;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a request for create/update cinema.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CinemaDataRequest {

  @Schema(example = "Kyivskyi")
  @NotNull(message = "Not valid name")
  @Pattern(regexp = "^[a-zA-Z ]{2,25}$",
      message = "Not valid name, only letters, length [2,25]")
  private String name;
  @Schema(example = "Kyiv")
  @NotNull(message = "Not valid city")
  @Pattern(regexp = "^[a-zA-Z ]{2,25}$",
      message = "Not valid city, only letters, length [2,25]")
  private String city;
  @Schema(example = "Khreschatyk street, 15")
  @NotNull(message = "Not valid streetAddress")
  @Pattern(regexp = "^[a-zA-Z0-9,. ]{2,25}$",
      message = "Not valid streetAddress")
  private String streetAddress;
}
