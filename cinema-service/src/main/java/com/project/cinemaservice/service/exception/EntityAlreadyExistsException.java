package com.project.cinemaservice.service.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents an exception specific to entity create/update operations. Extends GenericException.
 */
public class EntityAlreadyExistsException extends GenericException {

  public EntityAlreadyExistsException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}
