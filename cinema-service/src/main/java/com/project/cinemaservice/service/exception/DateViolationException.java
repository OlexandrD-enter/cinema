package com.project.cinemaservice.service.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents an exception specific to filter date data. Extends GenericException.
 */
public class DateViolationException extends GenericException {

  public DateViolationException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}