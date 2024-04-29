package com.project.cinemaservice.service.execption;

import org.springframework.http.HttpStatus;

public class GenreAlreadyExistsException extends GenericException {

  public GenreAlreadyExistsException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}
