package com.despachatec.exceptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  // Manejar excepciones específicas
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorDetails> handleResourceNotFoundException(
      ResourceNotFoundException exception,
      WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(
        new Date(),
        exception.getMessage(),
        request.getDescription(false));

    return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(DespachatecAPIException.class)
  public ResponseEntity<ErrorDetails> handleBlogAPIException(
      DespachatecAPIException exception,
      WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(
        new Date(),
        exception.getMessage(),
        request.getDescription(false));

    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }

  // Manejar excepciones globales
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorDetails> handleGlobalException(
      Exception exception,
      WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(
        new Date(),
        exception.getMessage(),
        request.getDescription(false));

    return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String message = error.getDefaultMessage();
      errors.put(fieldName, message);
    });

    ValidationErrorDetails errorDetails = new ValidationErrorDetails(
        new Date(),
        "Error de validación",
        request.getDescription(false),
        errors);

    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }

  // Clase interna para detalles de error
  public static class ErrorDetails {
    private Date timestamp;
    private String message;
    private String details;

    public ErrorDetails(Date timestamp, String message, String details) {
      this.timestamp = timestamp;
      this.message = message;
      this.details = details;
    }

    public Date getTimestamp() {
      return timestamp;
    }

    public String getMessage() {
      return message;
    }

    public String getDetails() {
      return details;
    }
  }

  // Clase para errores de validación
  public static class ValidationErrorDetails extends ErrorDetails {
    private Map<String, String> errors;

    public ValidationErrorDetails(
        Date timestamp,
        String message,
        String details,
        Map<String, String> errors) {
      super(timestamp, message, details);
      this.errors = errors;
    }

    public Map<String, String> getErrors() {
      return errors;
    }
  }
}
