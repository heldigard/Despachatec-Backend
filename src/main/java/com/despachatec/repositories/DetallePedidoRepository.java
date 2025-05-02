package com.despachatec.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.despachatec.models.DetallePedido;
import com.despachatec.models.Pedido;
import com.despachatec.models.Producto;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
  List<DetallePedido> findByPedido(Pedido pedido);

  List<DetallePedido> findByProducto(Producto producto);
}
