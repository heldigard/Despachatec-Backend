package com.despachatec.controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.despachatec.dto.JwtAuthResponse;
import com.despachatec.dto.LoginRequest;
import com.despachatec.dto.RegisterRequest;
import com.despachatec.models.Rol;
import com.despachatec.models.Usuario;
import com.despachatec.repositories.RolRepository;
import com.despachatec.repositories.UsuarioRepository;
import com.despachatec.security.JwtTokenProvider;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private UsuarioRepository usuarioRepository;

  @Autowired
  private RolRepository rolRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JwtTokenProvider tokenProvider;

  @PostMapping("/login")
  public ResponseEntity<JwtAuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequest.getUsernameOrEmail(),
            loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    // Obtener token
    String token = tokenProvider.generateToken(authentication);

    // Obtener detalles del usuario para la respuesta
    String username = authentication.getName();
    Usuario usuario = usuarioRepository.findByUsernameOrEmail(username, username)
        .orElseThrow();

    return ResponseEntity.ok(new JwtAuthResponse(token, usuario.getUsername(), usuario.getNombre(), usuario.getId()));
  }

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
    // Comprobar si ya existe un usuario con ese username
    if (usuarioRepository.existsByUsername(registerRequest.getUsername())) {
      return new ResponseEntity<>("El nombre de usuario ya está en uso", HttpStatus.BAD_REQUEST);
    }

    // Comprobar si ya existe un usuario con ese email
    if (usuarioRepository.existsByEmail(registerRequest.getEmail())) {
      return new ResponseEntity<>("El email ya está en uso", HttpStatus.BAD_REQUEST);
    }

    // Crear nuevo usuario
    Usuario usuario = new Usuario();
    usuario.setNombre(registerRequest.getNombre());
    usuario.setUsername(registerRequest.getUsername());
    usuario.setEmail(registerRequest.getEmail());
    usuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

    // Asignar rol de usuario por defecto
    Rol roles = rolRepository.findByNombre("USER")
        .orElseGet(() -> {
          Rol userRol = new Rol();
          userRol.setNombre("USER");
          return rolRepository.save(userRol);
        });

    usuario.setRoles(Collections.singleton(roles));
    usuarioRepository.save(usuario);

    Map<String, Object> response = new HashMap<>();
    response.put("mensaje", "Usuario registrado correctamente");
    response.put("usuario", usuario);

    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }
}
