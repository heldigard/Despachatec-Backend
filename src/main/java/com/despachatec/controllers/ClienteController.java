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

import com.despachatec.dto.ClienteDTO;
import com.despachatec.models.Cliente;
import com.despachatec.repositories.ClienteRepository;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

  @Autowired
  private ClienteRepository clienteRepository;

  @GetMapping
  @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
  public List<ClienteDTO> getAllClientes() {
    return clienteRepository.findAll().stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
  public ResponseEntity<ClienteDTO> getClienteById(@PathVariable Long id) {
    return clienteRepository.findById(id)
        .map(cliente -> ResponseEntity.ok(convertToDTO(cliente)))
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ClienteDTO> createCliente(@Valid @RequestBody ClienteDTO clienteDTO) {
    // Verificar si ya existe un cliente con ese email
    if (clienteDTO.getEmail() != null && clienteRepository.existsByEmail(clienteDTO.getEmail())) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    Cliente cliente = convertToEntity(clienteDTO);
    Cliente savedCliente = clienteRepository.save(cliente);

    return new ResponseEntity<>(convertToDTO(savedCliente), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ClienteDTO> updateCliente(
      @PathVariable Long id,
      @Valid @RequestBody ClienteDTO clienteDTO) {

    return clienteRepository.findById(id)
        .map(cliente -> {
          // Verificar si ya existe otro cliente con ese email
          if (clienteDTO.getEmail() != null &&
              !cliente.getEmail().equals(clienteDTO.getEmail()) &&
              clienteRepository.existsByEmail(clienteDTO.getEmail())) {
            return new ResponseEntity<ClienteDTO>(HttpStatus.BAD_REQUEST);
          }

          cliente.setNombre(clienteDTO.getNombre());
          cliente.setApellidos(clienteDTO.getApellidos());
          cliente.setEmail(clienteDTO.getEmail());
          cliente.setTelefono(clienteDTO.getTelefono());
          cliente.setDireccion(clienteDTO.getDireccion());

          Cliente updatedCliente = clienteRepository.save(cliente);
          return ResponseEntity.ok(convertToDTO(updatedCliente));
        })
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Map<String, Boolean>> deleteCliente(@PathVariable Long id) {
    return clienteRepository.findById(id)
        .map(cliente -> {
          clienteRepository.delete(cliente);

          Map<String, Boolean> response = new HashMap<>();
          response.put("deleted", Boolean.TRUE);
          return ResponseEntity.ok(response);
        })
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/search")
  @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
  public List<ClienteDTO> searchClientes(@RequestParam String query) {
    return clienteRepository.findByNombreContainingOrApellidosContaining(query, query).stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  // Método para convertir Entity a DTO
  private ClienteDTO convertToDTO(Cliente cliente) {
    ClienteDTO clienteDTO = new ClienteDTO();
    clienteDTO.setId(cliente.getId());
    clienteDTO.setNombre(cliente.getNombre());
    clienteDTO.setApellidos(cliente.getApellidos());
    clienteDTO.setEmail(cliente.getEmail());
    clienteDTO.setTelefono(cliente.getTelefono());
    clienteDTO.setDireccion(cliente.getDireccion());
    return clienteDTO;
  }

  // Método para convertir DTO a Entity
  private Cliente convertToEntity(ClienteDTO clienteDTO) {
    Cliente cliente = new Cliente();
    cliente.setId(clienteDTO.getId());
    cliente.setNombre(clienteDTO.getNombre());
    cliente.setApellidos(clienteDTO.getApellidos());
    cliente.setEmail(clienteDTO.getEmail());
    cliente.setTelefono(clienteDTO.getTelefono());
    cliente.setDireccion(clienteDTO.getDireccion());
    return cliente;
  }
}
