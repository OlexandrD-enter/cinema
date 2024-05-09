package com.project.cinemaservice.service.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents an exception specific to booking passed showtime. Extends GenericException.
 */
public class ShowtimeAlreadyStartedException extends GenericException {

  public ShowtimeAlreadyStartedException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}
