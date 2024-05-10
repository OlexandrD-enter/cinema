package com.project.userservice.messaging;

import com.project.userservice.messaging.event.UserEmailVerificationEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for publishing user events to RabbitMQ.
 */
@Service
public class UserEventPublisher {

  private final RabbitTemplate rabbitTemplate;
  private final String userExchange;
  private final String userEmailVerificationRoutingKey;

  public UserEventPublisher(RabbitTemplate rabbitTemplate,
      @Value("${rabbitmq.user.email-verification.exchange}") String userExchange,
      @Value("${rabbitmq.user.email-verification.routing-key}") String userEmailVerificationRoutingKey) {
    this.rabbitTemplate = rabbitTemplate;
    this.userExchange = userExchange;
    this.userEmailVerificationRoutingKey = userEmailVerificationRoutingKey;
  }

  public void sendEmailVerificationEvent(UserEmailVerificationEvent userEmailVerificationEvent) {
    rabbitTemplate.convertAndSend(userExchange, userEmailVerificationRoutingKey,
        userEmailVerificationEvent);
  }
}
