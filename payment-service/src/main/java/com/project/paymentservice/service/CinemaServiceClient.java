package com.project.paymentservice.service;

import com.project.paymentservice.domain.dto.order.OrderClientDetails;
import com.project.paymentservice.service.exception.CinemaServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * Service class responsible for interacting with the cinema service.
 */
@Service
@RequiredArgsConstructor
public class CinemaServiceClient {

  private final WebClient cinemaWebClient;

  public OrderClientDetails getOrderDetails(Long orderId) {
    return cinemaWebClient.get()
        .uri("/api/v1/orders/{orderId}", orderId)
        .retrieve()
        .bodyToMono(OrderClientDetails.class)
        .onErrorMap(WebClientRequestException.class,
            e -> new CinemaServiceException("Media service call exception " + e.getMessage()))
        .onErrorMap(WebClientResponseException.class,
            e -> new CinemaServiceException("Media service call exception " + e.getMessage()))
        .blockOptional()
        .orElseThrow(() -> new CinemaServiceException("Media service call exception"));
  }
}
