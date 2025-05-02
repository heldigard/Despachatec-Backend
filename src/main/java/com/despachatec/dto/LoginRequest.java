package com.despachatec.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class LoginRequest {

  @NotBlank(message = "El nombre de usuario es obligatorio")
  private String usernameOrEmail;

  @NotBlank(message = "La contraseña es obligatoria")
  private String password;
}
