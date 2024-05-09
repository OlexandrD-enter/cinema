package com.project.cinemaservice.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents an event for order reservation.
 */
@Getter
@AllArgsConstructor
public class OrderReservationEvent {

  private Long orderId;
}
