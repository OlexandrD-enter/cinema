package com.project.paymentservice.domain.dto.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a request for payment.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
  @NotNull(message = "Order id should be greater then 0")
  @Min(value = 1, message = "Order id should be greater then 0")
  private Long orderId;
}
