package com.project.userservice.service.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents an exception specific to logout operations. Extends GenericException.
 */
public class UserAlreadyExistsException extends GenericException {

  public UserAlreadyExistsException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}
