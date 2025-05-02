package com.despachatec.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.despachatec.models.Cliente;
import com.despachatec.models.Empleado;
import com.despachatec.models.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
  List<Pedido> findByCliente(Cliente cliente);

  List<Pedido> findByEmpleado(Empleado empleado);

  List<Pedido> findByEstado(Pedido.EstadoPedido estado);

  List<Pedido> findByFechaPedidoBetween(Date inicio, Date fin);

  List<Pedido> findByClienteAndEstado(Cliente cliente, Pedido.EstadoPedido estado);
}
