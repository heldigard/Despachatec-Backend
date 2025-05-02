package com.despachatec.models;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "productos")
public class Producto {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String nombre;

  @Column(columnDefinition = "TEXT")
  private String descripcion;

  @Column(nullable = false)
  private BigDecimal precio;

  @Column(name = "imagen_url")
  private String imagenUrl;

  @Column(name = "categoria")
  private String categoria;

  @Column(name = "stock_disponible")
  private Integer stockDisponible;

  @Column(name = "esta_activo")
  private Boolean estaActivo = true;
}
