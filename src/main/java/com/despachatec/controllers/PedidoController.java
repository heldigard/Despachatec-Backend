package com.despachatec.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.despachatec.dto.ApiResponse;
import com.despachatec.dto.DetallePedidoDTO;
import com.despachatec.dto.PedidoDTO;
import com.despachatec.models.Cliente;
import com.despachatec.models.DetallePedido;
import com.despachatec.models.Empleado;
import com.despachatec.models.Pedido;
import com.despachatec.models.Producto;
import com.despachatec.repositories.ClienteRepository;
import com.despachatec.repositories.DetallePedidoRepository;
import com.despachatec.repositories.EmpleadoRepository;
import com.despachatec.repositories.PedidoRepository;
import com.despachatec.repositories.ProductoRepository;
import com.despachatec.utils.ApiResponseBuilder;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

  @Autowired
  private PedidoRepository pedidoRepository;

  @Autowired
  private ClienteRepository clienteRepository;

  @Autowired
  private EmpleadoRepository empleadoRepository;

  @Autowired
  private ProductoRepository productoRepository;

  @Autowired
  private DetallePedidoRepository detallePedidoRepository;

  @GetMapping
  @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
  public ResponseEntity<ApiResponse> getAllPedidos() {
    List<PedidoDTO> pedidos = pedidoRepository.findAll().stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
    return ApiResponseBuilder.success("Lista de pedidos obtenida", pedidos);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
  public ResponseEntity<ApiResponse> getPedidoById(@PathVariable Long id) {
    return pedidoRepository.findById(id)
        .map(pedido -> ApiResponseBuilder.success("Pedido encontrado", convertToDTO(pedido)))
        .orElse(ApiResponseBuilder.notFound("Pedido", id.toString()));
  }

  @GetMapping("/cliente/{clienteId}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
  public ResponseEntity<ApiResponse> getPedidosByCliente(@PathVariable Long clienteId) {
    Cliente cliente = clienteRepository.findById(clienteId).orElse(null);
    if (cliente != null) {
      List<PedidoDTO> pedidos = pedidoRepository.findByCliente(cliente).stream()
          .map(this::convertToDTO)
          .collect(Collectors.toList());
      return ApiResponseBuilder.success("Pedidos del cliente obtenidos", pedidos);
    }
    return ApiResponseBuilder.notFound("Cliente", clienteId.toString());
  }

  @GetMapping("/estado/{estado}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
  public ResponseEntity<ApiResponse> getPedidosByEstado(@PathVariable String estado) {
    Pedido.EstadoPedido estadoEnum;
    try {
      estadoEnum = Pedido.EstadoPedido.valueOf(estado.toUpperCase());
    } catch (IllegalArgumentException e) {
      return ApiResponseBuilder.error("Estado de pedido inválido", HttpStatus.BAD_REQUEST);
    }
    List<PedidoDTO> pedidos = pedidoRepository.findByEstado(estadoEnum).stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
    return ApiResponseBuilder.success("Pedidos por estado obtenidos", pedidos);
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
  @Transactional
  public ResponseEntity<ApiResponse> createPedido(@Valid @RequestBody PedidoDTO pedidoDTO) {
    try {
      Cliente cliente = clienteRepository.findById(pedidoDTO.getClienteId())
          .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
      Pedido pedido = new Pedido();
      pedido.setCliente(cliente);
      pedido.setFechaPedido(new Date());
      if (pedidoDTO.getEmpleadoId() != null) {
        Empleado empleado = empleadoRepository.findById(pedidoDTO.getEmpleadoId()).orElse(null);
        pedido.setEmpleado(empleado);
      }
      pedido.setEstado(Pedido.EstadoPedido.PENDIENTE);
      Pedido savedPedido = pedidoRepository.save(pedido);
      if (pedidoDTO.getDetalles() != null && !pedidoDTO.getDetalles().isEmpty()) {
        for (DetallePedidoDTO detalleDTO : pedidoDTO.getDetalles()) {
          Producto producto = productoRepository.findById(detalleDTO.getProductoId())
              .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
          if (producto.getStockDisponible() < detalleDTO.getCantidad()) {
            throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
          }
          DetallePedido detalle = new DetallePedido();
          detalle.setPedido(savedPedido);
          detalle.setProducto(producto);
          detalle.setCantidad(detalleDTO.getCantidad());
          detalle.setPrecioUnitario(producto.getPrecio());
          detallePedidoRepository.save(detalle);
          producto.setStockDisponible(producto.getStockDisponible() - detalleDTO.getCantidad());
          productoRepository.save(producto);
          savedPedido.getDetalles().add(detalle);
        }
      } else {
        return ApiResponseBuilder.error("El pedido debe tener al menos un detalle", HttpStatus.BAD_REQUEST);
      }
      BigDecimal total = savedPedido.getDetalles().stream()
          .map(DetallePedido::calcularSubtotal)
          .reduce(BigDecimal.ZERO, BigDecimal::add);
      savedPedido.setTotal(total);
      pedidoRepository.save(savedPedido);
      return ApiResponseBuilder.created("Pedido", convertToDTO(savedPedido));
    } catch (RuntimeException e) {
      return ApiResponseBuilder.error(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  @PutMapping("/{id}/estado")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse> actualizarEstadoPedido(
      @PathVariable Long id,
      @RequestParam String estado) {
    Pedido.EstadoPedido estadoEnum;
    try {
      estadoEnum = Pedido.EstadoPedido.valueOf(estado.toUpperCase());
    } catch (IllegalArgumentException e) {
      return ApiResponseBuilder.error("Estado de pedido inválido", HttpStatus.BAD_REQUEST);
    }
    return pedidoRepository.findById(id)
        .map(pedido -> {
          pedido.setEstado(estadoEnum);
          if (estadoEnum == Pedido.EstadoPedido.ENTREGADO) {
            pedido.setFechaEntrega(new Date());
          }
          Pedido updatedPedido = pedidoRepository.save(pedido);
          return ApiResponseBuilder.updated("Pedido", convertToDTO(updatedPedido));
        })
        .orElse(ApiResponseBuilder.notFound("Pedido", id.toString()));
  }

  @PutMapping("/{id}/asignar-empleado/{empleadoId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse> asignarEmpleadoAPedido(
      @PathVariable Long id,
      @PathVariable Long empleadoId) {
    Pedido pedido = pedidoRepository.findById(id).orElse(null);
    Empleado empleado = empleadoRepository.findById(empleadoId).orElse(null);
    if (pedido == null) {
      return ApiResponseBuilder.notFound("Pedido", id.toString());
    }
    if (empleado == null) {
      return ApiResponseBuilder.notFound("Empleado", empleadoId.toString());
    }
    pedido.setEmpleado(empleado);
    Pedido updatedPedido = pedidoRepository.save(pedido);
    return ApiResponseBuilder.updated("Pedido", convertToDTO(updatedPedido));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public ResponseEntity<ApiResponse> deletePedido(@PathVariable Long id) {
    return pedidoRepository.findById(id)
        .map(pedido -> {
          if (pedido.getEstado() == Pedido.EstadoPedido.PENDIENTE) {
            for (DetallePedido detalle : pedido.getDetalles()) {
              Producto producto = detalle.getProducto();
              producto.setStockDisponible(producto.getStockDisponible() + detalle.getCantidad());
              productoRepository.save(producto);
            }
            pedidoRepository.delete(pedido);
            return ApiResponseBuilder.deleted("Pedido");
          } else {
            return ApiResponseBuilder.error("Solo se pueden eliminar pedidos en estado PENDIENTE",
                HttpStatus.BAD_REQUEST);
          }
        })
        .orElse(ApiResponseBuilder.notFound("Pedido", id.toString()));
  }

  // Método para convertir Entity a DTO
  private PedidoDTO convertToDTO(Pedido pedido) {
    PedidoDTO pedidoDTO = new PedidoDTO();
    pedidoDTO.setId(pedido.getId());
    pedidoDTO.setClienteId(pedido.getCliente().getId());

    if (pedido.getEmpleado() != null) {
      pedidoDTO.setEmpleadoId(pedido.getEmpleado().getId());
    }

    pedidoDTO.setFechaPedido(pedido.getFechaPedido());
    pedidoDTO.setFechaEntrega(pedido.getFechaEntrega());
    pedidoDTO.setEstado(pedido.getEstado());
    pedidoDTO.setTotal(pedido.getTotal());

    // Convertir los detalles
    List<DetallePedidoDTO> detallesDTO = new ArrayList<>();
    for (DetallePedido detalle : pedido.getDetalles()) {
      DetallePedidoDTO detalleDTO = new DetallePedidoDTO();
      detalleDTO.setId(detalle.getId());
      detalleDTO.setPedidoId(detalle.getPedido().getId());
      detalleDTO.setProductoId(detalle.getProducto().getId());
      detalleDTO.setCantidad(detalle.getCantidad());
      detalleDTO.setPrecioUnitario(detalle.getPrecioUnitario());
      detalleDTO.setSubtotal(detalle.calcularSubtotal());

      // Agregar información del producto
      detalleDTO.setNombreProducto(detalle.getProducto().getNombre());
      detalleDTO.setDescripcionProducto(detalle.getProducto().getDescripcion());

      detallesDTO.add(detalleDTO);
    }

    pedidoDTO.setDetalles(detallesDTO);

    return pedidoDTO;
  }
}
