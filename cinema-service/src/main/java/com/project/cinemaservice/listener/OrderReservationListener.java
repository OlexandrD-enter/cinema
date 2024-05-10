package com.project.cinemaservice.listener;

import com.project.cinemaservice.messaging.event.OrderReservationEvent;
import com.project.cinemaservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Argument;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Listener component for handling order reservation events.
 */
@Component
@RequiredArgsConstructor
public class OrderReservationListener {

  private final OrderService orderService;

  @RabbitListener(
      bindings = @QueueBinding(
          value = @Queue(value = "${rabbitmq.order.reservation.queue}", durable = "true"),
          exchange = @Exchange(value = "${rabbitmq.order.reservation.exchange}",
              type = "x-delayed-message",
              arguments = {@Argument(name = "x-delayed-type", value = "direct")}),
          key = "${rabbitmq.order.reservation.routing-key}"
      )
  )
  public void listener(OrderReservationEvent orderReservationEvent) {
    orderService.checkIfOrderPaid(orderReservationEvent.getOrderId());
  }
}
