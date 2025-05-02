package com.despachatec.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoDTO {

  private Long id;

  @NotNull(message = "El pedido es obligatorio")
  private Long pedidoId;

  @NotNull(message = "El producto es obligatorio")
  private Long productoId;

  @NotNull(message = "La cantidad es obligatoria")
  @Min(value = 1, message = "La cantidad debe ser al menos 1")
  private Integer cantidad;

  private BigDecimal precioUnitario;

  private BigDecimal subtotal;

  // Campos adicionales para mostrar información del producto en la respuesta
  private String nombreProducto;
  private String descripcionProducto;
}
