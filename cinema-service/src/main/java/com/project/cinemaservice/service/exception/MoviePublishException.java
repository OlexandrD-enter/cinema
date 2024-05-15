package com.project.cinemaservice.service.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents an exception specific to movie publish status. Extends GenericException.
 */
public class MoviePublishException extends GenericException {

  public MoviePublishException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}