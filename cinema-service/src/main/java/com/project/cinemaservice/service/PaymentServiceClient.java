package com.project.cinemaservice.service;

import com.project.cinemaservice.domain.dto.order.OrderRefundRequest;
import com.project.cinemaservice.service.exception.MediaServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * Service class responsible for interacting with the payment service.
 */
@Service
@RequiredArgsConstructor
public class PaymentServiceClient {

  private final WebClient paymentWebClient;

  public Void refundPayment(String transactionId) {
    return paymentWebClient.post()
        .uri("/api/v1/payments/refund")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(new OrderRefundRequest(transactionId)))
        .retrieve()
        .bodyToMono(Void.class)
        .onErrorMap(WebClientRequestException.class,
            e -> new MediaServiceException("Payment service call exception" + e.getMessage()))
        .onErrorMap(WebClientResponseException.class,
            e -> new MediaServiceException("Payment service call exception" + e.getMessage()))
        .block();
  }
}
