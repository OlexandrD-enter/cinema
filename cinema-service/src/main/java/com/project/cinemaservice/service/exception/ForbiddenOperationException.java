package com.project.cinemaservice.service.exception;

import org.springframework.http.HttpStatus;

/**
 * An exception representing an attempt to perform a forbidden operation. Extends GenericException.
 */
public class ForbiddenOperationException extends GenericException {

  public ForbiddenOperationException(String message) {
    super(message);
    status = HttpStatus.FORBIDDEN;
  }
}
