package com.despachatec.dto;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoDTO {

  private Long id;

  @NotBlank(message = "El nombre es obligatorio")
  private String nombre;

  @NotBlank(message = "Los apellidos son obligatorios")
  private String apellidos;

  @NotBlank(message = "El DNI es obligatorio")
  private String dni;

  private String telefono;

  private String direccion;

  @PastOrPresent(message = "La fecha de contratación no puede ser futura")
  private Date fechaContratacion;

  private String cargo;

  @Positive(message = "El salario debe ser un valor positivo")
  private Double salario;

  private Long usuarioId;
}
