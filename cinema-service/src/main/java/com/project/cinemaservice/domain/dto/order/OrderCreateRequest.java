package com.project.cinemaservice.domain.dto.order;

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

  private Long showTimeId;
  private List<Long> selectedRoomSeatsIds;
}
