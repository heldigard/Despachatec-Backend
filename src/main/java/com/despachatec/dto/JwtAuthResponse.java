package com.despachatec.dto;

import java.util.Set;

import lombok.Data;

@Data
public class JwtAuthResponse {
  private String accessToken;
  private String tokenType = "Bearer";
  private String username;
  private String nombre;
  private Long id;
  private Set<String> roles;

  public JwtAuthResponse(String accessToken, String username, String nombre, Long id, Set<String> roles) {
    this.accessToken = accessToken;
    this.username = username;
    this.nombre = nombre;
    this.id = id;
    this.roles = roles;
  }
}
