package com.project.notificationservice.listener;

import com.project.notificationservice.domain.dto.email.OrderedTicketsDetails;
import com.project.notificationservice.domain.dto.order.OrderedTickets;
import com.project.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * A component class responsible for listening to order event messages and processing them.
 */
@Component
@RequiredArgsConstructor
public class OrderListener {

  private final EmailService emailService;

  @RabbitListener(
      bindings = @QueueBinding(
          value = @Queue(value = "${rabbitmq.user.paid-tickets.queue}", durable = "true"),
          exchange = @Exchange(value = "${rabbitmq.user.paid-tickets.exchange}",
              type = "topic"),
          key = "${rabbitmq.user.paid-tickets.routing-key}"
      )
  )
  public void orderedTicketsListener(OrderedTickets orderedTickets) {
    emailService.sendEmail(
        new OrderedTicketsDetails(orderedTickets.getOwnerEmail(), orderedTickets));
  }
}
