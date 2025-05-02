package com.despachatec.dto;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {

  private Long id;

  @NotBlank(message = "El nombre es obligatorio")
  private String nombre;

  private String descripcion;

  @NotNull(message = "El precio es obligatorio")
  @Min(value = 0, message = "El precio debe ser mayor o igual a cero")
  private BigDecimal precio;

  private String imagenUrl;

  private String categoria;

  @Min(value = 0, message = "El stock disponible debe ser mayor o igual a cero")
  private Integer stockDisponible;

  private Boolean estaActivo = true;
}
