package com.project.cinemaservice.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Represents an event for order payment confirmation.
 */
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class OrderPaymentConfirmEvent {

  private Long orderId;
  private String transactionId;
}