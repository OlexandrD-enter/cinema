package com.project.cinemaservice.service.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents an exception specific to room seat create/update operations. Extends GenericException.
 */
public class SeatNumberAlreadyExistsException extends GenericException {

  public SeatNumberAlreadyExistsException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}
