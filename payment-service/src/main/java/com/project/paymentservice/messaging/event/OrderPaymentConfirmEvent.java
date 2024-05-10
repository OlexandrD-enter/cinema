package com.project.paymentservice.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Represents the paid order event
 */
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class OrderPaymentConfirmEvent {

  private Long orderId;
}
