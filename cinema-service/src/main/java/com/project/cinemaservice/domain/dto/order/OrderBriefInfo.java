package com.project.cinemaservice.domain.dto.order;

import com.project.cinemaservice.persistence.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an order client brief info details.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderBriefInfo {

  private Long id;
  private LocalDateTime orderCreationTime;
  private OrderStatus orderStatus;
  private String cinemaName;
  private String cinemaCity;
  private String cinemaAddress;
  private String movieName;
  private LocalDateTime showtimeStartDate;
  private BigDecimal totalPrice;
}
