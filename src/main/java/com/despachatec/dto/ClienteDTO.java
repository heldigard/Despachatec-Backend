package com.despachatec.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {

  private Long id;

  @NotBlank(message = "El nombre es obligatorio")
  private String nombre;

  @NotBlank(message = "Los apellidos son obligatorios")
  private String apellidos;

  @Email(message = "El formato del email es inválido")
  private String email;

  private String telefono;

  private String direccion;
}
