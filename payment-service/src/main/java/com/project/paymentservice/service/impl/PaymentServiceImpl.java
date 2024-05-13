package com.project.paymentservice.service.impl;

import com.project.paymentservice.domain.dto.order.OrderClientDetails;
import com.project.paymentservice.domain.dto.order.OrderStatus;
import com.project.paymentservice.domain.dto.payment.PaymentRequest;
import com.project.paymentservice.domain.dto.payment.PaymentResponse;
import com.project.paymentservice.messaging.OrderEventPublisher;
import com.project.paymentservice.messaging.event.OrderPaymentConfirmEvent;
import com.project.paymentservice.service.CinemaServiceClient;
import com.project.paymentservice.service.PaymentService;
import com.project.paymentservice.service.exception.IllegalOrderStatusException;
import com.project.paymentservice.service.exception.StripeCustomException;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.Refund;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.LineItem;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * PaymentService implementation responsible for payment related operations.
 */
@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

  private final String paymentConfiguration;
  private final Long expirationSessionTimeInSeconds;
  private final String successUrl;
  private final String failureUrl;
  private final String webhookEndpointSecret;
  private final CinemaServiceClient cinemaServiceClient;
  private final OrderEventPublisher orderEventPublisher;

  /**
   * Constructor for PaymentServiceImpl.
   *
   * @param stripeApiSecretKey              Stripe API secret key
   * @param paymentConfiguration           Payment configuration
   * @param expirationSessionTimeInSeconds  Expiration session time in seconds
   * @param successUrl                      Success URL for payment
   * @param failureUrl                      Failure URL for payment
   * @param webhookEndpointSecret          Webhook endpoint secret
   * @param cinemaServiceClient            Client for interacting with the cinema service
   * @param orderEventPublisher            Publisher for order events
   */
  public PaymentServiceImpl(@Value("${stripe.secret-key}") String stripeApiSecretKey,
      @Value("${stripe.payment.configuration}") String paymentConfiguration,
      @Value("${stripe.payment.session-expiration}") Long expirationSessionTimeInSeconds,
      @Value("${stripe.payment.success-url}") String successUrl,
      @Value("${stripe.payment.failure-url}") String failureUrl,
      @Value("${stripe.webhook-secret}") String webhookEndpointSecret,
      CinemaServiceClient cinemaServiceClient, OrderEventPublisher orderEventPublisher) {
    this.paymentConfiguration = paymentConfiguration;
    this.expirationSessionTimeInSeconds = expirationSessionTimeInSeconds;
    this.successUrl = successUrl;
    this.failureUrl = failureUrl;
    this.webhookEndpointSecret = webhookEndpointSecret;
    this.cinemaServiceClient = cinemaServiceClient;
    this.orderEventPublisher = orderEventPublisher;

    Stripe.apiKey = stripeApiSecretKey;
  }

  @Override
  public PaymentResponse pay(PaymentRequest paymentRequest) {
    OrderClientDetails order = cinemaServiceClient.getOrderDetails(paymentRequest.getOrderId());
    if (!order.getOrderStatus().equals(OrderStatus.RESERVED)) {
      throw new IllegalOrderStatusException("Invalid order status for payment");
    }
    List<LineItem> lineItems = createLineItems(order);
    long expirationSessionTime = calculateExpirationTime();

    try {
      SessionCreateParams params =
          SessionCreateParams.builder()
              .setMode(SessionCreateParams.Mode.PAYMENT)
              .setSuccessUrl(successUrl)
              .setCancelUrl(failureUrl)
              .setPaymentMethodConfiguration(paymentConfiguration)
              .addAllLineItem(lineItems)
              .setExpiresAt(expirationSessionTime)
              .putMetadata("order_id", String.valueOf(order.getId()))
              .build();

      Session session = Session.create(params);
      return new PaymentResponse(session.getUrl());
    } catch (StripeException e) {
      throw new StripeCustomException(e.getMessage());
    }
  }

  @Override
  public void checkPaymentConfirmation(String payload, String sigHeader) {
    if (sigHeader == null) {
      throw new StripeCustomException("Bad sigHeader");
    }
    Event event;
    try {
      event = Webhook.constructEvent(payload, sigHeader, webhookEndpointSecret);
    } catch (SignatureVerificationException e) {
      throw new StripeCustomException("Error verifying webhook signature: " + e.getMessage());
    }

    EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
    StripeObject stripeObject;
    if (dataObjectDeserializer.getObject().isPresent()) {
      stripeObject = dataObjectDeserializer.getObject().get();
    } else {
      throw new StripeCustomException("Serialization event error");
    }

    if (stripeObject instanceof Session session) {
      if (!session.getPaymentStatus().equals("paid")) {
        throw new StripeCustomException("Order not paid");
      }
      Map<String, String> metadata = session.getMetadata();
      Long orderId = Long.valueOf(metadata.get("order_id"));
      if (orderId != null) {
        orderEventPublisher.sendOrderPaymentConfirmEvent(new OrderPaymentConfirmEvent(orderId,
            session.getPaymentIntent()));
      } else {
        throw new StripeCustomException("Order ID not found in session metadata");
      }
    }
  }

  @Override
  public void refundPayment(String transactionId) {
    try {
      RefundCreateParams params = RefundCreateParams.builder()
          .setPaymentIntent(transactionId)
          .build();
      Refund refund = Refund.create(params);

      if (!refund.getStatus().equals("succeeded")) {
        throw new StripeCustomException(String.format(
            "Refund for transaction %s not success", transactionId));
      }
    } catch (StripeException e) {
      throw new StripeCustomException(e.getMessage());
    }
  }

  private List<LineItem> createLineItems(OrderClientDetails order) {
    BigDecimal pricePerSeat = order.getTotalPrice()
        .divide(BigDecimal.valueOf(order.getBookedSeatNumberIds().size()), RoundingMode.HALF_UP);

    return List.of(SessionCreateParams.LineItem.builder()
        .setQuantity((long) order.getBookedSeatNumberIds().size())
        .setPriceData(createPriceData(order.getMoviePreviewUrl(), order.getMovieName(),
            pricePerSeat))
        .build());
  }

  private PriceData createPriceData(String previewUrl, String movieName, BigDecimal price) {
    return PriceData.builder()
        .setProductData(
            PriceData.ProductData.builder()
                .addImage(previewUrl)
                .setName(movieName)
                .build()
        )
        .setCurrency("UAH")
        .setUnitAmountDecimal(price.multiply(BigDecimal.valueOf(100)))
        .build();
  }

  private long calculateExpirationTime() {
    return Instant.now().plusSeconds(expirationSessionTimeInSeconds).getEpochSecond();
  }
}
