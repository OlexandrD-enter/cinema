package com.project.cinemaservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.project.cinemaservice.service.exception.MediaServiceException;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class PaymentServiceClientTest {

  private MockWebServer mockWebServer;
  private PaymentServiceClient paymentServiceClient;

  @BeforeEach
  void setUp() throws Exception {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    WebClient webClient = WebClient.builder()
        .baseUrl(mockWebServer.url("/").toString())
        .build();

    paymentServiceClient = new PaymentServiceClient(webClient);
  }

  @SneakyThrows
  @AfterEach
  void tearDown() {
    mockWebServer.shutdown();
  }

  @SneakyThrows
  @Test
  void refundPayment_Success() {
    mockWebServer.enqueue(new MockResponse().setResponseCode(200));

    paymentServiceClient.refundPayment("transactionId_1");

    RecordedRequest request = mockWebServer.takeRequest();

    assertEquals("POST", request.getMethod());
    assertEquals("/api/v1/payments/refund", request.getPath());
    assertEquals("{\"transactionId\":\"transactionId_1\"}", request.getBody().readUtf8());
  }

  @Test
  void refundPayment_WithError() {
    mockWebServer.enqueue(new MockResponse().setResponseCode(500));

    assertThrows(MediaServiceException.class,
        () -> paymentServiceClient.refundPayment("transactionId"));
  }
}
