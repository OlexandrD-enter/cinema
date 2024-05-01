package com.project.cinemaservice.service.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents an exception specific to file convert operations. Extends GenericException.
 */
public class FileConvertException extends GenericException {

  public FileConvertException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}