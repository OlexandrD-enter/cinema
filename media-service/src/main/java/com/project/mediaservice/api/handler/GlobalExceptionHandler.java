package com.project.mediaservice.api.handler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.project.mediaservice.api.exception.CommonExceptionInfo;
import com.project.mediaservice.service.exception.GenericException;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Global exception handler for handling specific exceptions and providing custom error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  public static final String METHOD_ARGUMENT_TYPE_MISMATCH_MESSAGE =
      "The field '%s' must have a valid type of '%s'. Wrong value is '%s'";

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

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<CommonExceptionInfo> handleEntityNotFoundException(
      EntityNotFoundException ex) {
    return new ResponseEntity<>(new CommonExceptionInfo(NOT_FOUND.value(), ex.getMessage()),
        NOT_FOUND);
  }

  /**
   * The given method intercepts MethodArgumentTypeMismatchException, which occurs when an argument
   * of a controller method has wrong type. For example, id must have a representation of Long (i.e
   * "5"), but was inconvertible String (i.e "asdf").
   *
   * @param ex TypeMismatchException that is intercepted by this method.
   * @return ResponseEntity with details about TypeMismatchException.
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<CommonExceptionInfo> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex) {
    String actualField = ex.getName();
    Object wrongValue = Optional.ofNullable(ex.getValue()).orElse("");
    String requiredType = "";
    Class<?> requiredTypeClass = ex.getRequiredType();
    if (requiredTypeClass != null) {
      requiredType = requiredTypeClass.getSimpleName();
    }
    String message = String.format(METHOD_ARGUMENT_TYPE_MISMATCH_MESSAGE, actualField,
        requiredType, wrongValue);
    CommonExceptionInfo appException = new CommonExceptionInfo(BAD_REQUEST.value(), message);
    return new ResponseEntity<>(appException, BAD_REQUEST);
  }
}
