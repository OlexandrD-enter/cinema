package com.project.cinemaservice.domain.dto.order;

import com.project.cinemaservice.persistence.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a brief info about order and its status.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusDetails {

  private Long id;
  private OrderStatus orderStatus;
}
