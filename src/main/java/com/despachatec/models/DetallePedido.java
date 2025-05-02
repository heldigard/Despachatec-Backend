package com.despachatec.models;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "detalles_pedido")
public class DetallePedido {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "pedido_id", nullable = false)
  private Pedido pedido;

  @ManyToOne
  @JoinColumn(name = "producto_id", nullable = false)
  private Producto producto;

  @Column(nullable = false)
  private Integer cantidad;

  @Column(name = "precio_unitario", nullable = false)
  private BigDecimal precioUnitario;

  // Método para calcular el subtotal del detalle
  public BigDecimal calcularSubtotal() {
    return precioUnitario.multiply(new BigDecimal(cantidad));
  }
}
