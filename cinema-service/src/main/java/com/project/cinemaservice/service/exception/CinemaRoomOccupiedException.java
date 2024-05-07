package com.project.cinemaservice.service.exception;

import org.springframework.http.HttpStatus;

public class CinemaRoomOccupiedException extends GenericException {

  public CinemaRoomOccupiedException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}

