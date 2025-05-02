package com.despachatec.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.despachatec.dto.EmpleadoDTO;
import com.despachatec.models.Empleado;
import com.despachatec.models.Usuario;
import com.despachatec.repositories.EmpleadoRepository;
import com.despachatec.repositories.UsuarioRepository;

@RestController
@RequestMapping("/api/empleados")
@PreAuthorize("hasRole('ADMIN')")
public class EmpleadoController {

  @Autowired
  private EmpleadoRepository empleadoRepository;

  @Autowired
  private UsuarioRepository usuarioRepository;

  @GetMapping
  public List<EmpleadoDTO> getAllEmpleados() {
    return empleadoRepository.findAll().stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  @GetMapping("/{id}")
  public ResponseEntity<EmpleadoDTO> getEmpleadoById(@PathVariable Long id) {
    return empleadoRepository.findById(id)
        .map(empleado -> ResponseEntity.ok(convertToDTO(empleado)))
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/cargo/{cargo}")
  public List<EmpleadoDTO> getEmpleadosByCargo(@PathVariable String cargo) {
    return empleadoRepository.findByCargo(cargo).stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  @GetMapping("/search")
  public List<EmpleadoDTO> searchEmpleados(@RequestParam String query) {
    return empleadoRepository.findByNombreContainingOrApellidosContaining(query, query).stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  @PostMapping
  public ResponseEntity<EmpleadoDTO> createEmpleado(@Valid @RequestBody EmpleadoDTO empleadoDTO) {
    // Verificar si ya existe un empleado con ese DNI
    if (empleadoDTO.getDni() != null && empleadoRepository.existsByDni(empleadoDTO.getDni())) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    Empleado empleado = convertToEntity(empleadoDTO);

    // Asociar con usuario si se proporciona ID de usuario
    if (empleadoDTO.getUsuarioId() != null) {
      Usuario usuario = usuarioRepository.findById(empleadoDTO.getUsuarioId())
          .orElse(null);
      empleado.setUsuario(usuario);
    }

    Empleado savedEmpleado = empleadoRepository.save(empleado);
    return new ResponseEntity<>(convertToDTO(savedEmpleado), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<EmpleadoDTO> updateEmpleado(
      @PathVariable Long id,
      @Valid @RequestBody EmpleadoDTO empleadoDTO) {

    return empleadoRepository.findById(id)
        .map(empleado -> {
          // Verificar si ya existe otro empleado con ese DNI
          if (empleadoDTO.getDni() != null &&
              !empleado.getDni().equals(empleadoDTO.getDni()) &&
              empleadoRepository.existsByDni(empleadoDTO.getDni())) {
            return new ResponseEntity<EmpleadoDTO>(HttpStatus.BAD_REQUEST);
          }

          empleado.setNombre(empleadoDTO.getNombre());
          empleado.setApellidos(empleadoDTO.getApellidos());
          empleado.setDni(empleadoDTO.getDni());
          empleado.setTelefono(empleadoDTO.getTelefono());
          empleado.setDireccion(empleadoDTO.getDireccion());
          empleado.setFechaContratacion(empleadoDTO.getFechaContratacion());
          empleado.setCargo(empleadoDTO.getCargo());
          empleado.setSalario(empleadoDTO.getSalario());

          // Actualizar asociación con usuario si cambia
          if (empleadoDTO.getUsuarioId() != null &&
              (empleado.getUsuario() == null || !empleado.getUsuario().getId().equals(empleadoDTO.getUsuarioId()))) {
            Usuario usuario = usuarioRepository.findById(empleadoDTO.getUsuarioId())
                .orElse(null);
            empleado.setUsuario(usuario);
          }

          Empleado updatedEmpleado = empleadoRepository.save(empleado);
          return ResponseEntity.ok(convertToDTO(updatedEmpleado));
        })
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Map<String, Boolean>> deleteEmpleado(@PathVariable Long id) {
    return empleadoRepository.findById(id)
        .map(empleado -> {
          empleadoRepository.delete(empleado);

          Map<String, Boolean> response = new HashMap<>();
          response.put("deleted", Boolean.TRUE);
          return ResponseEntity.ok(response);
        })
        .orElse(ResponseEntity.notFound().build());
  }

  // Método para convertir Entity a DTO
  private EmpleadoDTO convertToDTO(Empleado empleado) {
    EmpleadoDTO empleadoDTO = new EmpleadoDTO();
    empleadoDTO.setId(empleado.getId());
    empleadoDTO.setNombre(empleado.getNombre());
    empleadoDTO.setApellidos(empleado.getApellidos());
    empleadoDTO.setDni(empleado.getDni());
    empleadoDTO.setTelefono(empleado.getTelefono());
    empleadoDTO.setDireccion(empleado.getDireccion());
    empleadoDTO.setFechaContratacion(empleado.getFechaContratacion());
    empleadoDTO.setCargo(empleado.getCargo());
    empleadoDTO.setSalario(empleado.getSalario());

    if (empleado.getUsuario() != null) {
      empleadoDTO.setUsuarioId(empleado.getUsuario().getId());
    }

    return empleadoDTO;
  }

  // Método para convertir DTO a Entity
  private Empleado convertToEntity(EmpleadoDTO empleadoDTO) {
    Empleado empleado = new Empleado();
    empleado.setId(empleadoDTO.getId());
    empleado.setNombre(empleadoDTO.getNombre());
    empleado.setApellidos(empleadoDTO.getApellidos());
    empleado.setDni(empleadoDTO.getDni());
    empleado.setTelefono(empleadoDTO.getTelefono());
    empleado.setDireccion(empleadoDTO.getDireccion());
    empleado.setFechaContratacion(empleadoDTO.getFechaContratacion());
    empleado.setCargo(empleadoDTO.getCargo());
    empleado.setSalario(empleadoDTO.getSalario());

    return empleado;
  }
}
