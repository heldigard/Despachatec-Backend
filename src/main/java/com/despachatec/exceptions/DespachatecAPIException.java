package com.despachatec.exceptions;

import org.springframework.http.HttpStatus;

public class DespachatecAPIException extends RuntimeException {

  private HttpStatus status;
  private String message;

  public DespachatecAPIException(HttpStatus status, String message) {
    super(message);
    this.status = status;
    this.message = message;
  }

  public DespachatecAPIException(String message, HttpStatus status, String message1) {
    super(message);
    this.status = status;
    this.message = message1;
  }

  public HttpStatus getStatus() {
    return status;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
