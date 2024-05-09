package com.project.cinemaservice.messaging;

import com.project.cinemaservice.messaging.event.OrderReservationEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for publishing order reservation events with a delay.
 */
@Service
public class OrderReservationEventPublisher {

  private final RabbitTemplate rabbitTemplate;
  private final String orderExchange;
  private final String orderReservationRoutingKey;
  private final Long reservationTimeInMinutes;

  /**
   * Constructs an OrderReservationEventPublisher.
   *
   * @param rabbitTemplate            The RabbitTemplate for sending messages
   * @param orderExchange             The exchange to publish order reservation events
   * @param orderReservationRoutingKey The routing key for order reservation events
   * @param reservationTimeInMinutes  The time in minutes to delay the reservation event
   */
  public OrderReservationEventPublisher(RabbitTemplate rabbitTemplate,
      @Value("${rabbitmq.order.reservation.exchange}") String orderExchange,
      @Value("${rabbitmq.order.reservation.routing-key}") String orderReservationRoutingKey,
      @Value("${orders.reservation-time}") Long reservationTimeInMinutes) {
    this.rabbitTemplate = rabbitTemplate;
    this.orderExchange = orderExchange;
    this.orderReservationRoutingKey = orderReservationRoutingKey;
    this.reservationTimeInMinutes = reservationTimeInMinutes;
  }

  /**
   * Sends an order reservation event with a delay based on configured reservation time.
   *
   * @param orderReservationEvent The order reservation event to send
   */
  public void sendOrderReservationEvent(OrderReservationEvent orderReservationEvent) {
    rabbitTemplate.convertAndSend(orderExchange, orderReservationRoutingKey, orderReservationEvent,
        message -> {
          message.getMessageProperties().setDelayLong(1000L * 60 * reservationTimeInMinutes);
          return message;
        }
    );
  }
}
