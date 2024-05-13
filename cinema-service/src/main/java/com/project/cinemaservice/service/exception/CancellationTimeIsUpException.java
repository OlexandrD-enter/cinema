package com.project.cinemaservice.service.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents an exception specific to elapsed time to cancel the order. Extends GenericException.
 */
public class CancellationTimeIsUpException extends GenericException {

  public CancellationTimeIsUpException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}

