package com.project.userservice.service.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents an exception specific to user token when it exists in db. Extends GenericException.
 */
public class UserTokenAlreadyExistsException extends GenericException {

  public UserTokenAlreadyExistsException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}
