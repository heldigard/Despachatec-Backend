package com.despachatec.controllers;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.despachatec.dto.ApiResponse;
import com.despachatec.models.Cliente;
import com.despachatec.models.DetallePedido;
import com.despachatec.models.Pedido;
import com.despachatec.models.Producto;
import com.despachatec.repositories.ClienteRepository;
import com.despachatec.repositories.PedidoRepository;
import com.despachatec.repositories.ProductoRepository;

@RestController
@RequestMapping("/api/reportes")
@PreAuthorize("hasRole('ADMIN')")
public class ReporteController {

  @Autowired
  private PedidoRepository pedidoRepository;

  @Autowired
  private ProductoRepository productoRepository;

  @Autowired
  private ClienteRepository clienteRepository;

  @GetMapping("/ventas-por-periodo")
  public ResponseEntity<ApiResponse> getVentasPorPeriodo(
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaInicio,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin) {

    // Ajustar fechaFin para incluir todo el día
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(fechaFin);
    calendar.add(Calendar.DAY_OF_MONTH, 1);
    calendar.add(Calendar.SECOND, -1);
    Date fechaFinAjustada = calendar.getTime();

    // Obtener pedidos en el periodo
    List<Pedido> pedidos = pedidoRepository.findByFechaPedidoBetween(fechaInicio, fechaFinAjustada);

    // Calcular estadísticas
    BigDecimal ventasTotales = pedidos.stream()
        .map(Pedido::getTotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    long numeroPedidos = pedidos.size();

    Map<Pedido.EstadoPedido, Long> pedidosPorEstado = pedidos.stream()
        .collect(Collectors.groupingBy(
            Pedido::getEstado,
            Collectors.counting()));

    // Preparar respuesta
    Map<String, Object> estadisticas = new HashMap<>();
    estadisticas.put("ventasTotales", ventasTotales);
    estadisticas.put("numeroPedidos", numeroPedidos);
    estadisticas.put("pedidosPorEstado", pedidosPorEstado);
    estadisticas.put("fechaInicio", fechaInicio);
    estadisticas.put("fechaFin", fechaFin);

    return ResponseEntity.ok(new ApiResponse(true, "Estadísticas de ventas generadas con éxito", estadisticas));
  }

  @GetMapping("/productos-mas-vendidos")
  public ResponseEntity<ApiResponse> getProductosMasVendidos(
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaInicio,
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin,
      @RequestParam(defaultValue = "10") int limite) {

    List<Pedido> pedidos;

    if (fechaInicio != null && fechaFin != null) {
      // Ajustar fechaFin para incluir todo el día
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(fechaFin);
      calendar.add(Calendar.DAY_OF_MONTH, 1);
      calendar.add(Calendar.SECOND, -1);
      Date fechaFinAjustada = calendar.getTime();

      pedidos = pedidoRepository.findByFechaPedidoBetween(fechaInicio, fechaFinAjustada);
    } else {
      pedidos = pedidoRepository.findAll();
    }

    // Mapear producto a cantidad vendida
    Map<Producto, Integer> ventasPorProducto = new HashMap<>();

    for (Pedido pedido : pedidos) {
      for (DetallePedido detalle : pedido.getDetalles()) {
        Producto producto = detalle.getProducto();
        ventasPorProducto.put(producto,
            ventasPorProducto.getOrDefault(producto, 0) + detalle.getCantidad());
      }
    }

    // Ordenar por cantidad y limitar
    List<Map<String, Object>> productosMasVendidos = ventasPorProducto.entrySet().stream()
        .sorted(Map.Entry.<Producto, Integer>comparingByValue().reversed())
        .limit(limite)
        .map(entry -> {
          Map<String, Object> item = new HashMap<>();
          Producto p = entry.getKey();
          item.put("id", p.getId());
          item.put("nombre", p.getNombre());
          item.put("categoria", p.getCategoria());
          item.put("cantidadVendida", entry.getValue());
          item.put("ingresos", p.getPrecio().multiply(new BigDecimal(entry.getValue())));
          return item;
        })
        .collect(Collectors.toList());

    return ResponseEntity.ok(new ApiResponse(true, "Productos más vendidos generados con éxito", productosMasVendidos));
  }

  @GetMapping("/clientes-frecuentes")
  public ResponseEntity<ApiResponse> getClientesFrecuentes(
      @RequestParam(defaultValue = "10") int limite) {

    List<Cliente> clientes = clienteRepository.findAll();

    // Mapear cliente a número de pedidos
    Map<Cliente, Long> pedidosPorCliente = new HashMap<>();

    for (Cliente cliente : clientes) {
      long numeroPedidos = pedidoRepository.findByCliente(cliente).size();
      pedidosPorCliente.put(cliente, numeroPedidos);
    }

    // Ordenar por número de pedidos y limitar
    List<Map<String, Object>> clientesFrecuentes = pedidosPorCliente.entrySet().stream()
        .sorted(Map.Entry.<Cliente, Long>comparingByValue().reversed())
        .limit(limite)
        .map(entry -> {
          Map<String, Object> item = new HashMap<>();
          Cliente c = entry.getKey();
          item.put("id", c.getId());
          item.put("nombre", c.getNombre() + " " + c.getApellidos());
          item.put("email", c.getEmail());
          item.put("numeroPedidos", entry.getValue());

          // Calcular gasto total del cliente
          BigDecimal gastoTotal = pedidoRepository.findByCliente(c).stream()
              .map(Pedido::getTotal)
              .reduce(BigDecimal.ZERO, BigDecimal::add);

          item.put("gastoTotal", gastoTotal);
          return item;
        })
        .collect(Collectors.toList());

    return ResponseEntity.ok(new ApiResponse(true, "Clientes frecuentes generados con éxito", clientesFrecuentes));
  }

  @GetMapping("/resumen-inventario")
  public ResponseEntity<ApiResponse> getResumenInventario() {
    List<Producto> productos = productoRepository.findAll();

    // Estadísticas generales
    long totalProductos = productos.size();
    long productosAgotados = productos.stream()
        .filter(p -> p.getStockDisponible() <= 0)
        .count();
    long productosBajoStock = productos.stream()
        .filter(p -> p.getStockDisponible() > 0 && p.getStockDisponible() < 10)
        .count();

    Map<String, Long> productosPorCategoria = productos.stream()
        .collect(Collectors.groupingBy(
            Producto::getCategoria,
            Collectors.counting()));

    // Productos agotados o con bajo stock
    List<Map<String, Object>> alertasStock = productos.stream()
        .filter(p -> p.getStockDisponible() < 10)
        .map(p -> {
          Map<String, Object> item = new HashMap<>();
          item.put("id", p.getId());
          item.put("nombre", p.getNombre());
          item.put("categoria", p.getCategoria());
          item.put("stockActual", p.getStockDisponible());
          item.put("estado", p.getStockDisponible() <= 0 ? "AGOTADO" : "BAJO_STOCK");
          return item;
        })
        .collect(Collectors.toList());

    // Preparar respuesta
    Map<String, Object> resumen = new HashMap<>();
    resumen.put("totalProductos", totalProductos);
    resumen.put("productosAgotados", productosAgotados);
    resumen.put("productosBajoStock", productosBajoStock);
    resumen.put("productosPorCategoria", productosPorCategoria);
    resumen.put("alertasStock", alertasStock);

    return ResponseEntity.ok(new ApiResponse(true, "Resumen de inventario generado con éxito", resumen));
  }
}
