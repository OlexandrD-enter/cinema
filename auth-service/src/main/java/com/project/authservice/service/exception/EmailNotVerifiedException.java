package com.project.authservice.service.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents an exception specific to email verification. Extends RuntimeException.
 */
public class EmailNotVerifiedException extends GenericException {

  public EmailNotVerifiedException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}
