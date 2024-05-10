package com.project.paymentservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.project.paymentservice.domain.dto.order.OrderClientDetails;
import com.project.paymentservice.domain.dto.order.OrderStatus;
import com.project.paymentservice.service.exception.CinemaServiceException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

class CinemaServiceClientTest {

  private MockWebServer mockWebServer;
  private CinemaServiceClient cinemaServiceClient;

  @BeforeEach
  void setUp() throws Exception {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    String baseUrl = mockWebServer.url("/").toString();
    WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();

    cinemaServiceClient = new CinemaServiceClient(webClient);
  }

  @AfterEach
  void tearDown() throws Exception {
    mockWebServer.shutdown();
  }

  @Test
  void getOrderDetails_Success() {
    // Given
    Long orderId = 123L;
    String responseBody = "{\"id\": \"123\", \"movieName\": \"Movie Name\", \"orderStatus\": \"RESERVED\"}";
    mockWebServer.enqueue(new MockResponse()
        .setResponseCode(200)
        .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .setBody(responseBody));

    // When
    OrderClientDetails orderClientDetails = cinemaServiceClient.getOrderDetails(orderId);

    // Then
    assertEquals(orderId, orderClientDetails.getId());
    assertEquals("Movie Name", orderClientDetails.getMovieName());
    assertEquals(OrderStatus.RESERVED, orderClientDetails.getOrderStatus());
  }

  @Test
  void getOrderDetails_Error() {
    // Given
    mockWebServer.enqueue(new MockResponse().setResponseCode(500));

    // When & Then
    assertThrows(CinemaServiceException.class,
        () -> cinemaServiceClient.getOrderDetails(123L));
  }
}

