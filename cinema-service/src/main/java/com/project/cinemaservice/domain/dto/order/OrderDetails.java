package com.project.cinemaservice.domain.dto.order;

import com.project.cinemaservice.persistence.enums.OrderStatus;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an order client details response.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetails {

  private Long id;
  private OrderStatus orderStatus;
  private BigDecimal totalPrice;
  private String showtimeName;
  private Long moviePreviewFileId;
}
