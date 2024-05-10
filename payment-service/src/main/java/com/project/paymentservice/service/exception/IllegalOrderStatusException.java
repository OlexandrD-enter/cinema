package com.project.paymentservice.service.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents an exception specific to bad order status. Extends GenericException.
 */
public class IllegalOrderStatusException extends GenericException {

  public IllegalOrderStatusException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}
