package com.despachatec.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.despachatec.dto.ApiResponse;

/**
 * Clase de utilidad para crear respuestas API estandarizadas
 */
public class ApiResponseBuilder {

  /**
   * Genera una respuesta de éxito con mensaje
   */
  public static ResponseEntity<ApiResponse> success(String message) {
    ApiResponse response = new ApiResponse(true, message, new Date(), null);
    return ResponseEntity.ok(response);
  }

  /**
   * Genera una respuesta de éxito con mensaje y datos
   */
  public static ResponseEntity<ApiResponse> success(String message, Object data) {
    ApiResponse response = new ApiResponse(true, message, new Date(), data);
    return ResponseEntity.ok(response);
  }

  /**
   * Genera una respuesta de error con mensaje y estado HTTP
   */
  public static ResponseEntity<ApiResponse> error(String message, HttpStatus status) {
    ApiResponse response = new ApiResponse(false, message, new Date(), null);
    return new ResponseEntity<>(response, status);
  }

  /**
   * Genera una respuesta de error con mensaje, detalles de error y estado HTTP
   */
  public static ResponseEntity<ApiResponse> error(String message, Map<String, Object> errorDetails, HttpStatus status) {
    ApiResponse response = new ApiResponse(false, message, new Date(), errorDetails);
    return new ResponseEntity<>(response, status);
  }

  /**
   * Genera una respuesta de error con mensaje de validación
   */
  public static ResponseEntity<ApiResponse> validationError(String fieldName, String errorMessage) {
    Map<String, Object> errors = new HashMap<>();
    Map<String, String> validationErrors = new HashMap<>();
    validationErrors.put(fieldName, errorMessage);
    errors.put("validationErrors", validationErrors);

    ApiResponse response = new ApiResponse(false, "Error de validación", new Date(), errors);
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  /**
   * Genera una respuesta para recurso no encontrado
   */
  public static ResponseEntity<ApiResponse> notFound(String resourceName, String id) {
    String message = resourceName + " con id " + id + " no encontrado";
    ApiResponse response = new ApiResponse(false, message, new Date(), null);
    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  /**
   * Genera una respuesta para creación exitosa
   */
  public static ResponseEntity<ApiResponse> created(String resourceName, Object data) {
    String message = resourceName + " creado exitosamente";
    ApiResponse response = new ApiResponse(true, message, new Date(), data);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  /**
   * Genera una respuesta para actualización exitosa
   */
  public static ResponseEntity<ApiResponse> updated(String resourceName, Object data) {
    String message = resourceName + " actualizado exitosamente";
    ApiResponse response = new ApiResponse(true, message, new Date(), data);
    return ResponseEntity.ok(response);
  }

  /**
   * Genera una respuesta para eliminación exitosa
   */
  public static ResponseEntity<ApiResponse> deleted(String resourceName) {
    String message = resourceName + " eliminado exitosamente";
    ApiResponse response = new ApiResponse(true, message, new Date(), null);
    return ResponseEntity.ok(response);
  }
}
