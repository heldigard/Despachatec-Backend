package com.despachatec.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pedidos")
public class Pedido {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "cliente_id", nullable = false)
  private Cliente cliente;

  @ManyToOne
  @JoinColumn(name = "empleado_id")
  private Empleado empleado;

  @Column(name = "fecha_pedido")
  @Temporal(TemporalType.TIMESTAMP)
  private Date fechaPedido = new Date();

  @Column(name = "fecha_entrega")
  @Temporal(TemporalType.TIMESTAMP)
  private Date fechaEntrega;

  @Column(name = "estado")
  @Enumerated(EnumType.STRING)
  private EstadoPedido estado = EstadoPedido.PENDIENTE;

  @Column(name = "total")
  private BigDecimal total = BigDecimal.ZERO;

  @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<DetallePedido> detalles = new ArrayList<>();

  public enum EstadoPedido {
    PENDIENTE, PREPARANDO, LISTO, ENTREGADO, CANCELADO
  }

  // Método para agregar un detalle al pedido y actualizar el total
  public void agregarDetalle(DetallePedido detalle) {
    detalles.add(detalle);
    detalle.setPedido(this);
    recalcularTotal();
  }

  // Método para eliminar un detalle del pedido y actualizar el total
  public void eliminarDetalle(DetallePedido detalle) {
    detalles.remove(detalle);
    detalle.setPedido(null);
    recalcularTotal();
  }

  // Método para recalcular el total del pedido
  private void recalcularTotal() {
    total = detalles.stream()
        .map(detalle -> detalle.getPrecioUnitario().multiply(new BigDecimal(detalle.getCantidad())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
