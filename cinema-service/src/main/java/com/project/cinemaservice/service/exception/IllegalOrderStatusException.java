package com.project.cinemaservice.service.exception;

import org.springframework.http.HttpStatus;


/**
 * An exception representing an exception that indicates an attempt to set an illegal order status.
 * Extends GenericException.
 */
public class IllegalOrderStatusException extends GenericException {

  public IllegalOrderStatusException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}
