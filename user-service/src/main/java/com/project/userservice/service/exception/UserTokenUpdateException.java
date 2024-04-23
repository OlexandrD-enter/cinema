package com.project.userservice.service.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents an exception specific to user token when update it in db. Extends GenericException.
 */
public class UserTokenUpdateException extends GenericException {

  public UserTokenUpdateException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}