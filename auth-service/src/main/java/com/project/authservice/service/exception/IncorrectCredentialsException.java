package com.project.authservice.service.exception;

import org.springframework.http.HttpStatus;

/**
 * The given exception shows that the user provide bad credentials. Extends GenericException.
 */
public class IncorrectCredentialsException extends GenericException {

  public IncorrectCredentialsException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}
