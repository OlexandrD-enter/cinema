package com.project.paymentservice.service.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents an exception specific to payment operation. Extends GenericException.
 */
public class StripeCustomException extends GenericException {

  public StripeCustomException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}
