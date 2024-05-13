package com.project.cinemaservice.service.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents an exception specific to filter price data. Extends GenericException.
 */
public class PriceViolationException extends GenericException {

  public PriceViolationException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}