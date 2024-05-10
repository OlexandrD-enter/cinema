package com.project.notificationservice.listener;

import com.project.notificationservice.domain.dto.email.VerificationEmailDetails;
import com.project.notificationservice.domain.dto.user.UserEmailVerification;
import com.project.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * A component class responsible for listening to email verification messages and processing them.
 */
@Component
@RequiredArgsConstructor
public class RegistrationListener {

  private final EmailService emailService;

  @RabbitListener(
      bindings = @QueueBinding(
          value = @Queue(value = "${rabbitmq.user.email-verification.queue}", durable = "true"),
          exchange = @Exchange(value = "${rabbitmq.user.email-verification.exchange}", type = "topic"),
          key = "${rabbitmq.user.email-verification.routing-key}"
      )
  )
  public void registrationListener(UserEmailVerification userEmailVerification) {
    emailService.sendEmail(new VerificationEmailDetails(userEmailVerification.getEmail(),
        userEmailVerification.getToken()));
  }
}
