package com.project.cinemaservice.service.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents an exception specific to showtime operations. Extends GenericException.
 */
public class ShowtimeActionException extends GenericException {

  public ShowtimeActionException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}
