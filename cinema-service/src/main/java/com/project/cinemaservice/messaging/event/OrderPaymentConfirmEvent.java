package com.project.cinemaservice.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class OrderPaymentConfirmEvent {

  private Long orderId;
}