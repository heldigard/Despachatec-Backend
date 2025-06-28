package com.despachatec.controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
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
      // Autentica al usuario usando el nombre de usuario o email y la contraseña
      // proporcionados
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequest.getUsernameOrEmail(),
            loginRequest.getPassword()));

    // Si la autenticación es exitosa, se establece en el contexto de seguridad
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // Genera un token JWT para el usuario autenticado
    String token = tokenProvider.generateToken(authentication);

    // Obtiene el nombre de usuario (username o email) autenticado
    String username = authentication.getName();
    // Busca el usuario en la base de datos para obtener información adicional
    Usuario usuario = usuarioRepository.findByUsernameOrEmail(username, username)
        .orElseThrow();

    // Extrae los nombres de los roles del usuario
    Set<String> roles = usuario.getRoles().stream()
            .map(Rol::getNombre)
            .collect(Collectors.toSet());

    // Devuelve la respuesta con el token y los datos del usuario incluyendo roles
    return ResponseEntity
            .ok(new JwtAuthResponse(token, usuario.getUsername(), usuario.getNombre(), usuario.getId(), roles));
  }

  @PostMapping("/register")
  public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
      // Verifica si el nombre de usuario ya está en uso
      if (Boolean.TRUE.equals(usuarioRepository.existsByUsername(registerRequest.getUsername()))) {
          Map<String, Object> error = new HashMap<>();
          error.put("mensaje", "El nombre de usuario ya está en uso");
          return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Verifica si el email ya está en uso
    if (Boolean.TRUE.equals(usuarioRepository.existsByEmail(registerRequest.getEmail()))) {
        Map<String, Object> error = new HashMap<>();
        error.put("mensaje", "El email ya está en uso");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Crea un nuevo usuario con los datos proporcionados
    Usuario usuario = new Usuario();
    usuario.setNombre(registerRequest.getNombre());
    usuario.setUsername(registerRequest.getUsername());
    usuario.setEmail(registerRequest.getEmail());
    usuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

    // Asigna el rol USER por defecto
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

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletRequest request) {
      // No hay sesión que invalidar en JWT, pero se puede auditar o registrar el
      // logout si se desea
      return ResponseEntity.ok().body(Collections.singletonMap("mensaje", "Logout exitoso"));
  }
}
