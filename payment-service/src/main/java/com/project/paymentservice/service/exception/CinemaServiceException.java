package com.project.paymentservice.service.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents an exception specific to cinema-service operations. Extends GenericException.
 */
public class CinemaServiceException extends GenericException {

  public CinemaServiceException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}
