package com.project.cinemaservice.messaging;

import com.project.cinemaservice.messaging.event.OrderedTicketsEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for publishing order tickets event.
 */
@Service
public class OrderedTicketsEventPublisher {

  private final RabbitTemplate rabbitTemplate;
  private final String userExchange;
  private final String orderedTicketsRoutingKey;

  /**
   * Constructs the OrderedTicketsEventPublisher.
   *
   * @param rabbitTemplate           The RabbitTemplate for sending messages.
   * @param userExchange             The exchange for user-related events.
   * @param orderedTicketsRoutingKey The routing key for ordered tickets events.
   */
  public OrderedTicketsEventPublisher(RabbitTemplate rabbitTemplate,
      @Value("${rabbitmq.user.paid-tickets.exchange}") String userExchange,
      @Value("${rabbitmq.user.paid-tickets.routing-key}") String orderedTicketsRoutingKey) {
    this.rabbitTemplate = rabbitTemplate;
    this.userExchange = userExchange;
    this.orderedTicketsRoutingKey = orderedTicketsRoutingKey;
  }

  public void sendOrderedTicketsEvent(OrderedTicketsEvent orderedTicketsEvent) {
    rabbitTemplate.convertAndSend(userExchange, orderedTicketsRoutingKey, orderedTicketsEvent);
  }
}
