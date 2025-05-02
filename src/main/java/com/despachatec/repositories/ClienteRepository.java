package com.despachatec.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.despachatec.models.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
  Optional<Cliente> findByEmail(String email);

  List<Cliente> findByNombreContainingOrApellidosContaining(String nombre, String apellidos);

  Boolean existsByEmail(String email);
}
