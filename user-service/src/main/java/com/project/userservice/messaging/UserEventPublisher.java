package com.project.userservice.messaging;

import com.project.userservice.messaging.event.UserEmailVerification;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserEventPublisher {

  private final RabbitTemplate rabbitTemplate;
  private final String userExchange;
  private final String userEmailVerificationRoutingKey;

  public UserEventPublisher(RabbitTemplate rabbitTemplate,
      @Value("${rabbitmq.exchange.user}") String userExchange,
      @Value("${rabbitmq.routing-key.email-verification:}") String userEmailVerificationRoutingKey) {
    this.rabbitTemplate = rabbitTemplate;
    this.userExchange = userExchange;
    this.userEmailVerificationRoutingKey = userEmailVerificationRoutingKey;
  }

  public void sendEmailVerificationEvent(String userEmail, String token) {
    rabbitTemplate.convertAndSend(userExchange, userEmailVerificationRoutingKey,
        new UserEmailVerification(userEmail, token));
  }
}
