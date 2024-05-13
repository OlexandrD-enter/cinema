package com.project.cinemaservice.domain.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

/**
 * Represents an order admin brief info details.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderBriefInfoAdmin {

  private Page<OrderBriefInfo> orders;
  private LocalDateTime minCreationDate;
  private LocalDateTime maxCreationDate;
  private BigDecimal minTotalSum;
  private BigDecimal maxTotalSum;
}
