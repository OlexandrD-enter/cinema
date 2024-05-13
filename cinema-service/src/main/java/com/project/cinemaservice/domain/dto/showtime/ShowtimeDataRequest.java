package com.project.cinemaservice.domain.dto.showtime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a showtime data request for create/edit.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowtimeDataRequest {

  @Schema(example = "1")
  @NotNull(message = "Not valid movieId")
  @Min(value = 1, message = "MovieId number must be greater than 0")
  private Long movieId;
  @Schema(example = "1")
  @NotNull(message = "Not valid cinemaRoomId")
  @Min(value = 1, message = "CinemaRoomId number must be greater than 0")
  private Long cinemaRoomId;
  @Schema(example = "150.00")
  @Digits(integer = 4, fraction = 2)
  @DecimalMin(value = "0.00", inclusive = false, message = "price must be greater than 0.00")
  private BigDecimal price;
  @Schema(example = "2024-05-07T13:00:00")
  @NotNull(message = "StartDate must not be null")
  private LocalDateTime startDate;
}
