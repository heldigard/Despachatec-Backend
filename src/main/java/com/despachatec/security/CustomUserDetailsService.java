package com.despachatec.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.despachatec.models.Rol;
import com.despachatec.models.Usuario;
import com.despachatec.repositories.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  @Autowired
  private UsuarioRepository usuarioRepository;

  @Override
  public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
    Usuario usuario = usuarioRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
        .orElseThrow(
            () -> new UsernameNotFoundException("Usuario no encontrado con el username o email: " + usernameOrEmail));

    return new org.springframework.security.core.userdetails.User(
        usuario.getEmail(),
        usuario.getPassword(),
        mapRolesToAuthorities(usuario.getRoles()));
  }

  private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Rol> roles) {
    return roles.stream()
        .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getNombre()))
        .collect(Collectors.toList());
  }
}
