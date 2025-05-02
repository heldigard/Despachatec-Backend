package com.despachatec.dto;

import lombok.Data;

@Data
public class JwtAuthResponse {
  private String accessToken;
  private String tokenType = "Bearer";
  private String username;
  private String nombre;
  private Long id;

  public JwtAuthResponse(String accessToken, String username, String nombre, Long id) {
    this.accessToken = accessToken;
    this.username = username;
    this.nombre = nombre;
    this.id = id;
  }
}
