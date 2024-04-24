package com.project.cinemaservice.service.execption;

import org.springframework.http.HttpStatus;

/**
 * Represents an exception specific to cinema create/update operations. Extends GenericException.
 */
public class CinemaAlreadyExistsException extends GenericException {

  public CinemaAlreadyExistsException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}
