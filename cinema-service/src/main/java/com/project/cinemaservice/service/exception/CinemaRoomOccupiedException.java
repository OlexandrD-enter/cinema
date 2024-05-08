package com.project.cinemaservice.service.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents an exception specific to occupation of room for specific time interval. Extends
 * GenericException.
 */
public class CinemaRoomOccupiedException extends GenericException {

  public CinemaRoomOccupiedException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}

