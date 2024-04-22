package com.project.userservice.service.exception;

import org.springframework.http.HttpStatus;

/**
 * The given exception shows that the user action token not valid. Extends GenericException.
 */
public class NotValidUserToken extends GenericException {

  public NotValidUserToken(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}
