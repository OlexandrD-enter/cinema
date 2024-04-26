package com.project.mediaservice.service.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents an exception specific to file upload. Extends GenericException.
 */
public class FileUploadException extends GenericException {

  public FileUploadException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}
