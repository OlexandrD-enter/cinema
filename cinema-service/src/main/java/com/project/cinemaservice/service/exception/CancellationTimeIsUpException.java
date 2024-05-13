package com.project.cinemaservice.service.exception;

import org.springframework.http.HttpStatus;

public class CancellationTimeIsUpException extends GenericException {

  public CancellationTimeIsUpException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}

