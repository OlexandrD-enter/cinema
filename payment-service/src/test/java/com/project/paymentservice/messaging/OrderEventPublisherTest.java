package com.project.paymentservice.messaging;

import static org.mockito.Mockito.verify;

import com.project.paymentservice.messaging.event.OrderPaymentConfirmEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ExtendWith(MockitoExtension.class)
class OrderEventPublisherTest {

  @Mock
  RabbitTemplate rabbitTemplate;
  OrderEventPublisher orderEventPublisher;

  @BeforeEach
  public void setUp() {
    orderEventPublisher = new OrderEventPublisher(rabbitTemplate, "order.exchange",
        "order.payment.confirm");
  }

  @Test
  void sendOrderPaymentConfirmEvent_Success() {
    // Given
    OrderPaymentConfirmEvent orderPaymentConfirmEvent = new OrderPaymentConfirmEvent(1L,
        "paymentIntent");

    // When
    orderEventPublisher.sendOrderPaymentConfirmEvent(orderPaymentConfirmEvent);

    // Then
    verify(rabbitTemplate).convertAndSend("order.exchange", "order.payment.confirm",
        orderPaymentConfirmEvent);
  }
}

