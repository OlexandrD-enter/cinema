package com.project.userservice.service.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents an exception specific to Keycloak requests. Extends RuntimeException.
 */
public class KeycloakException extends GenericException {

  public KeycloakException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}
