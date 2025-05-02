package com.despachatec.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class RegisterRequest {

  @NotBlank(message = "El nombre es obligatorio")
  private String nombre;

  @NotBlank(message = "El nombre de usuario es obligatorio")
  @Size(min = 4, message = "El nombre de usuario debe tener al menos 4 caracteres")
  private String username;

  @NotBlank(message = "El email es obligatorio")
  @Email(message = "El formato del email es inválido")
  private String email;

  @NotBlank(message = "La contraseña es obligatoria")
  @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
  private String password;
}
