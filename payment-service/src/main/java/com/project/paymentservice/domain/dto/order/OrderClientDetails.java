package com.project.paymentservice.domain.dto.order;


import java.math.BigDecimal;
import java.util.List;
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
public class OrderClientDetails {

  private Long id;
  private OrderStatus orderStatus;
  private String movieName;
  private BigDecimal totalPrice;
  private String moviePreviewUrl;
  private List<Long> bookedSeatNumberIds;
}
