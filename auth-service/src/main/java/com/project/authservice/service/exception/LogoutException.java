package com.project.authservice.service.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents an exception specific to logout operations. Extends RuntimeException.
 */
public class LogoutException extends GenericException {

  public LogoutException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}
