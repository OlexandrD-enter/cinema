package com.project.cinemaservice.service.execption;

import org.springframework.http.HttpStatus;

/**
 * Represents an exception specific to media-service operations. Extends GenericException.
 */
public class MediaServiceException extends GenericException {

  public MediaServiceException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}
