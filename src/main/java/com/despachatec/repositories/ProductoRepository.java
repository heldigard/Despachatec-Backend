package com.despachatec.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.despachatec.models.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
  List<Producto> findByNombreContaining(String nombre);

  List<Producto> findByCategoria(String categoria);

  List<Producto> findByEstaActivoTrue();

  List<Producto> findByStockDisponibleGreaterThan(Integer cantidad);
}
