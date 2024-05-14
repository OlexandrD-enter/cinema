package com.project.cinemaservice.domain.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a request for create order.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {

  @Schema(example = "1")
  @NotNull(message = "Not valid showTimeId")
  @Min(1)
  private Long showTimeId;
  @Schema(example = "[1,2,3]")
  @NotNull(message = "Not valid selectedRoomSeatsIds")
  private List<Long> selectedRoomSeatsIds;
}
