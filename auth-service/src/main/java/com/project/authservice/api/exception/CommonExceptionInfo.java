package com.project.authservice.api.exception;

import java.time.LocalDateTime;
import lombok.Getter;

/**
 * Represents an application-specific exception with status, message, and timestamp information.
 */
@Getter
public class CommonExceptionInfo {

  private final int status;
  private final String message;
  private final LocalDateTime timestamp;

  /**
   * Constructs an CommonExceptionInfo object with the provided status and message, initializing the
   * timestamp to the current time.
   *
   * @param status  HTTP status code associated with the exception.
   * @param message Message describing the exception.
   */
  public CommonExceptionInfo(int status, String message) {
    this.status = status;
    this.message = message;
    this.timestamp = LocalDateTime.now();
  }
}
