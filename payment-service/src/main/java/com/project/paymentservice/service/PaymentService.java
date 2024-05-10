package com.project.paymentservice.service;


import com.project.paymentservice.domain.dto.payment.PaymentRequest;
import com.project.paymentservice.domain.dto.payment.PaymentResponse;

/**
 * Service interface for payment related operations.
 */
public interface PaymentService {

  PaymentResponse pay(PaymentRequest paymentRequest);

  void checkPaymentConfirmation(String payload, String sigHeader);

  void refundPayment(Long orderId);
}
