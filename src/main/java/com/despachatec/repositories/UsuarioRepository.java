package com.despachatec.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.despachatec.models.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
  Optional<Usuario> findByUsername(String username);

  Optional<Usuario> findByEmail(String email);

  Optional<Usuario> findByUsernameOrEmail(String username, String email);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);
}
