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

import com.despachatec.dto.ProductoDTO;
import com.despachatec.models.Producto;
import com.despachatec.repositories.ProductoRepository;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

  @Autowired
  private ProductoRepository productoRepository;

  @GetMapping
  public List<ProductoDTO> getAllProductos() {
    return productoRepository.findByEstaActivoTrue().stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductoDTO> getProductoById(@PathVariable Long id) {
    return productoRepository.findById(id)
        .map(producto -> ResponseEntity.ok(convertToDTO(producto)))
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/categoria/{categoria}")
  public List<ProductoDTO> getProductosByCategoria(@PathVariable String categoria) {
    return productoRepository.findByCategoria(categoria).stream()
        .filter(Producto::getEstaActivo)
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  @GetMapping("/search")
  public List<ProductoDTO> searchProductos(@RequestParam String query) {
    return productoRepository.findByNombreContaining(query).stream()
        .filter(Producto::getEstaActivo)
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ProductoDTO> createProducto(@Valid @RequestBody ProductoDTO productoDTO) {
    Producto producto = convertToEntity(productoDTO);
    Producto savedProducto = productoRepository.save(producto);

    return new ResponseEntity<>(convertToDTO(savedProducto), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ProductoDTO> updateProducto(
      @PathVariable Long id,
      @Valid @RequestBody ProductoDTO productoDTO) {

    return productoRepository.findById(id)
        .map(producto -> {
          producto.setNombre(productoDTO.getNombre());
          producto.setDescripcion(productoDTO.getDescripcion());
          producto.setPrecio(productoDTO.getPrecio());
          producto.setImagenUrl(productoDTO.getImagenUrl());
          producto.setCategoria(productoDTO.getCategoria());
          producto.setStockDisponible(productoDTO.getStockDisponible());
          producto.setEstaActivo(productoDTO.getEstaActivo());

          Producto updatedProducto = productoRepository.save(producto);
          return ResponseEntity.ok(convertToDTO(updatedProducto));
        })
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Map<String, Boolean>> deleteProducto(@PathVariable Long id) {
    return productoRepository.findById(id)
        .map(producto -> {
          // En lugar de eliminar físicamente, marcamos como inactivo
          producto.setEstaActivo(false);
          productoRepository.save(producto);

          Map<String, Boolean> response = new HashMap<>();
          response.put("deleted", Boolean.TRUE);
          return ResponseEntity.ok(response);
        })
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/admin/all")
  @PreAuthorize("hasRole('ADMIN')")
  public List<ProductoDTO> getAllProductosAdmin() {
    return productoRepository.findAll().stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  // Método para convertir Entity a DTO
  private ProductoDTO convertToDTO(Producto producto) {
    ProductoDTO productoDTO = new ProductoDTO();
    productoDTO.setId(producto.getId());
    productoDTO.setNombre(producto.getNombre());
    productoDTO.setDescripcion(producto.getDescripcion());
    productoDTO.setPrecio(producto.getPrecio());
    productoDTO.setImagenUrl(producto.getImagenUrl());
    productoDTO.setCategoria(producto.getCategoria());
    productoDTO.setStockDisponible(producto.getStockDisponible());
    productoDTO.setEstaActivo(producto.getEstaActivo());
    return productoDTO;
  }

  // Método para convertir DTO a Entity
  private Producto convertToEntity(ProductoDTO productoDTO) {
    Producto producto = new Producto();
    producto.setId(productoDTO.getId());
    producto.setNombre(productoDTO.getNombre());
    producto.setDescripcion(productoDTO.getDescripcion());
    producto.setPrecio(productoDTO.getPrecio());
    producto.setImagenUrl(productoDTO.getImagenUrl());
    producto.setCategoria(productoDTO.getCategoria());
    producto.setStockDisponible(productoDTO.getStockDisponible());
    producto.setEstaActivo(productoDTO.getEstaActivo() != null ? productoDTO.getEstaActivo() : true);
    return producto;
  }
}
