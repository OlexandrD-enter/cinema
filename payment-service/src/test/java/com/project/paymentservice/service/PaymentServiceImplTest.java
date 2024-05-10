package com.project.paymentservice.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.paymentservice.domain.dto.order.OrderClientDetails;
import com.project.paymentservice.domain.dto.order.OrderStatus;
import com.project.paymentservice.domain.dto.payment.PaymentRequest;
import com.project.paymentservice.domain.dto.payment.PaymentResponse;
import com.project.paymentservice.messaging.OrderEventPublisher;
import com.project.paymentservice.service.exception.IllegalOrderStatusException;
import com.project.paymentservice.service.exception.StripeCustomException;
import com.project.paymentservice.service.impl.PaymentServiceImpl;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import java.math.BigDecimal;
import java.util.Collections;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(classes = PaymentServiceImpl.class)
public class PaymentServiceImplTest {

  @MockBean
  private CinemaServiceClient cinemaServiceClient;

  @MockBean
  private OrderEventPublisher orderEventPublisher;

  @Autowired
  private PaymentServiceImpl paymentService;

  @Test
  void pay_SuccessfulPayment_ReturnsPaymentResponse() {
    // Given
    PaymentRequest paymentRequest = new PaymentRequest();
    paymentRequest.setOrderId(123L);
    OrderClientDetails orderDetails = new OrderClientDetails();
    orderDetails.setOrderStatus(OrderStatus.RESERVED);
    orderDetails.setTotalPrice(BigDecimal.valueOf(3500));
    orderDetails.setBookedSeatNumberIds(Collections.singletonList(1L));
    orderDetails.setMovieName("Test Movie");
    orderDetails.setMoviePreviewUrl("http://example.com/image.jpg");
    when(cinemaServiceClient.getOrderDetails(any())).thenReturn(orderDetails);

    // When
    PaymentResponse paymentResponse = paymentService.pay(paymentRequest);

    // Then
    assertNotNull(paymentResponse);
    assertTrue(paymentResponse.getSessionLink().contains("https://checkout.stripe.com/c/pay/"));
    verify(cinemaServiceClient, times(1)).getOrderDetails(123L);
  }

  @Test
  void pay_InvalidOrderStatus_ThrowsIllegalOrderStatusException() {
    // Given
    PaymentRequest paymentRequest = new PaymentRequest();
    paymentRequest.setOrderId(123L);
    OrderClientDetails orderDetails = new OrderClientDetails();
    orderDetails.setOrderStatus(OrderStatus.PAID);
    when(cinemaServiceClient.getOrderDetails(any())).thenReturn(orderDetails);

    // When
    assertThrows(IllegalOrderStatusException.class, () -> paymentService.pay(paymentRequest));

    // Then
    verify(cinemaServiceClient, times(1)).getOrderDetails(123L);
  }

  @Test
  void refundPayment_Success() {
    // Given
    String paymentIntent = generatePaymentIntent();
    completePayment(paymentIntent);

    // When & Then
    assertDoesNotThrow(() -> paymentService.refundPayment(paymentIntent));
  }

  @Test
  void refundPayment_ForNonExistTransaction_ThrowsStripeCustomException() {
    // Given
    String transactionId = "123";

    // When & Then
    assertThrows(StripeCustomException.class, () -> paymentService.refundPayment(transactionId));
  }

  @SneakyThrows
  private String generatePaymentIntent() {
    PaymentIntentCreateParams params =
        PaymentIntentCreateParams.builder()
            .setAmount(10000L)
            .setCurrency("UAH")
            .setAutomaticPaymentMethods(
                PaymentIntentCreateParams.AutomaticPaymentMethods
                    .builder()
                    .setEnabled(true)
                    .build()
            )
            .build();
    PaymentIntent paymentIntent = PaymentIntent.create(params);
    return paymentIntent.getId();
  }

  @SneakyThrows
  private void completePayment(String paymentId) {
    PaymentIntent resource = PaymentIntent.retrieve(paymentId);
    PaymentIntentConfirmParams params =
        PaymentIntentConfirmParams.builder()
            .setPaymentMethod("pm_card_visa")
            .setReturnUrl("https://www.example.com")
            .build();
    resource.confirm(params);
  }
}
