package com.project.paymentservice.api.controller;

import com.project.paymentservice.domain.dto.payment.PaymentRequest;
import com.project.paymentservice.domain.dto.payment.PaymentResponse;
import com.project.paymentservice.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller class that handles operations related to payment operations for customer.<br>
 * - POST /pay : Create session link for payment.<br>
 * - POST /stripe/events : Reacts to stripe payment events.<br>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

  private final PaymentService stripeService;

  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  @Operation(summary = "This method is used for pay by order")
  @PostMapping("/pay")
  public ResponseEntity<PaymentResponse> pay(@RequestBody @Valid PaymentRequest paymentRequest) {
    return ResponseEntity.ok(stripeService.pay(paymentRequest));
  }

  @PostMapping("/stripe/events")
  public void handleStripeEvent(@RequestBody String payload,
      @RequestHeader("Stripe-Signature") String sigHeader){
    stripeService.checkPaymentConfirmation(payload, sigHeader);
  }
}

