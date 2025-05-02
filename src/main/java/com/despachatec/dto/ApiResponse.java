package com.despachatec.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
  private boolean success;
  private String message;
  private Date timestamp;
  private Object data;

  public ApiResponse(boolean success, String message) {
    this.success = success;
    this.message = message;
    this.timestamp = new Date();
  }

  public ApiResponse(boolean success, String message, Object data) {
    this.success = success;
    this.message = message;
    this.data = data;
    this.timestamp = new Date();
  }
}
