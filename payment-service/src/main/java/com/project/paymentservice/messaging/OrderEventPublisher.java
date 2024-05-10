package com.project.paymentservice.messaging;

import com.project.paymentservice.messaging.event.OrderPaymentConfirmEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for publishing user events to RabbitMQ.
 */
@Service
public class OrderEventPublisher {

  private final RabbitTemplate rabbitTemplate;
  private final String orderExchange;
  private final String orderReservationRoutingKey;

  public OrderEventPublisher(RabbitTemplate rabbitTemplate,
      @Value("${rabbitmq.exchange.order}") String orderExchange,
      @Value("${rabbitmq.routing-key.order-reservation}") String orderReservationRoutingKey) {
    this.rabbitTemplate = rabbitTemplate;
    this.orderExchange = orderExchange;
    this.orderReservationRoutingKey = orderReservationRoutingKey;
  }

  public void sendOrderPaymentConfirmEvent(OrderPaymentConfirmEvent orderPaymentConfirmEvent) {
    rabbitTemplate.convertAndSend(orderExchange, orderReservationRoutingKey,
        orderPaymentConfirmEvent);
  }
}
