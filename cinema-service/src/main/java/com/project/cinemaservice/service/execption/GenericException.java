package com.project.cinemaservice.service.execption;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * The abstract exception represents specific-related exceptions. Extends RuntimeException
 */
@Getter
public abstract class GenericException extends RuntimeException {

  protected HttpStatus status;

  protected GenericException(String message) {
    super(message);
  }
}
