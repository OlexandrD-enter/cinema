package com.project.cinemaservice.service.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents an exception specific to genre create/update operations. Extends GenericException.
 */
public class GenreAlreadyExistsException extends GenericException {

  public GenreAlreadyExistsException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}
