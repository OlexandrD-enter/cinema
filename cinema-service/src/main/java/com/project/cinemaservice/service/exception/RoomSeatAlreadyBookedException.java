package com.project.cinemaservice.service.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents an exception specific to booking room seats. Extends GenericException.
 */
public class RoomSeatAlreadyBookedException extends GenericException {

  public RoomSeatAlreadyBookedException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}