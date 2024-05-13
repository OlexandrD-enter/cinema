package com.project.cinemaservice.domain.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents filter request for querying orders.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class OrderFilterRequest {

  private LocalDateTime fromOrderCreationTime;
  private LocalDateTime toOrderCreationTime;
  private String cinemaName;
  private String cinemaCity;
  private String movieName;
  private BigDecimal fromPrice;
  private BigDecimal toPrice;
}
