package com.project.cinemaservice.listener;

import com.project.cinemaservice.messaging.event.OrderPaymentConfirmEvent;
import com.project.cinemaservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Listener component for handling order payment confirmation events.
 */
@Component
@RequiredArgsConstructor
public class OrderPaymentConfirmationListener {

  private final OrderService orderService;

  @RabbitListener(
      bindings = @QueueBinding(
          value = @Queue(value = "${rabbitmq.order.payment.queue}", durable = "true"),
          exchange = @Exchange(value = "${rabbitmq.order.payment.exchange}", type = "topic"),
          key = "${rabbitmq.order.payment.routing-key}"
      )
  )
  public void paymentConfirmationListener(OrderPaymentConfirmEvent orderPaymentConfirmEvent) {
    orderService.confirmOrderPayment(orderPaymentConfirmEvent.getOrderId(),
        orderPaymentConfirmEvent.getTransactionId());
  }
}
