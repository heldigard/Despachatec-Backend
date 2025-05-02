package com.despachatec.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.despachatec.models.Pedido.EstadoPedido;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {

  private Long id;

  @NotNull(message = "El cliente es obligatorio")
  private Long clienteId;

  private Long empleadoId;

  private Date fechaPedido;

  private Date fechaEntrega;

  private EstadoPedido estado;

  private BigDecimal total;

  private List<DetallePedidoDTO> detalles = new ArrayList<>();
}
