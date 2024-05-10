package com.project.paymentservice.api.handler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.project.paymentservice.api.exception.CommonExceptionInfo;
import com.project.paymentservice.service.exception.GenericException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

  /**
   * Exception handler method for handling MethodArgumentNotValidException. It processes validation
   * errors and generates a custom response entity with error details.
   *
   * @param ex The MethodArgumentNotValidException instance caught during request processing.
   * @return ResponseEntity containing AppException with details of validation errors.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<CommonExceptionInfo> handleNotValidRegisterData(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    StringBuilder extractedMessages = new StringBuilder();

    for (String errorMessage : errors.values()) {
      String extractedMessage = errorMessage.replaceAll("\\{(.+?)=(.+?)}", "$2").trim();
      extractedMessages.append(extractedMessage).append(", ");
    }

    String finalMessage = extractedMessages.toString();
    if (finalMessage.endsWith(", ")) {
      finalMessage = finalMessage.substring(0, finalMessage.length() - 2);
    }

    return new ResponseEntity<>(new CommonExceptionInfo(BAD_REQUEST.value(), finalMessage),
        BAD_REQUEST);
  }
}
