package com.despachatec.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.despachatec.models.Empleado;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
  Optional<Empleado> findByDni(String dni);

  List<Empleado> findByCargo(String cargo);

  List<Empleado> findByNombreContainingOrApellidosContaining(String nombre, String apellidos);

  Boolean existsByDni(String dni);
}
