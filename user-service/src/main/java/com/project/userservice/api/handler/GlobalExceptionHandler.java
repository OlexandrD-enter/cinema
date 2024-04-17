package com.project.userservice.api.handler;

import com.project.userservice.api.exception.CommonExceptionInfo;
import com.project.userservice.service.exception.GenericException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for handling specific exceptions and providing custom error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(GenericException.class)
  public ResponseEntity<CommonExceptionInfo> handleGenericException(GenericException ex) {
    return new ResponseEntity<>(new CommonExceptionInfo(ex.getStatus().value(), ex.getMessage()),
        ex.getStatus());
  }
}
