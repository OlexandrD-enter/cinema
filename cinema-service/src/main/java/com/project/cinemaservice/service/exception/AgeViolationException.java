package com.project.cinemaservice.service.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents an exception specific to filter age data. Extends GenericException.
 */
public class AgeViolationException extends GenericException {

  public AgeViolationException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}
