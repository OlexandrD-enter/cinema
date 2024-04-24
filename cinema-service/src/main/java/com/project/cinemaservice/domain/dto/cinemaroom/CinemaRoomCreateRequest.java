package com.project.cinemaservice.domain.dto.cinemaroom;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a request for create cinema room.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CinemaRoomCreateRequest {

  @Schema(example = "Room 1")
  @NotNull(message = "Not valid name")
  @Pattern(regexp = "^[a-zA-Z0-9 ]{2,25}$",
      message = "Not valid name, only letters and nums, length [2,25]")
  private String name;
  @Schema(example = "ORDINARY")
  @NotNull(message = "Not valid roomType")
  @Pattern(regexp = "^(ORDINARY|VIP)$", message = "Room type must be either ORDINARY or VIP")
  private String roomType;
  @Schema(example = "1")
  @NotNull(message = "Not valid cinemaId")
  @Min(value = 1, message = "Cinema ID must be greater than 0")
  private Long cinemaId;
}
