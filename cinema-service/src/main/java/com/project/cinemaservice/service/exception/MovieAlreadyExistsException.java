package com.project.cinemaservice.service.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents an exception specific to movie create/update operations. Extends GenericException.
 */
public class MovieAlreadyExistsException extends GenericException {

  public MovieAlreadyExistsException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}
