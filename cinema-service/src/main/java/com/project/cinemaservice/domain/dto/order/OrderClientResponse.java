package com.project.cinemaservice.domain.dto.order;

import com.project.cinemaservice.persistence.enums.OrderStatus;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an order client response.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderClientResponse {

  private Long id;
  private OrderStatus orderStatus;
  private Long showtimeId;
  private List<Long> bookedSeatIds;
}
