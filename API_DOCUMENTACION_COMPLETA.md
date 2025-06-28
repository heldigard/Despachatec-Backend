# Despachatec-Backend API Reference - Documentación Completa

## Introducción
Esta documentación describe todos los endpoints de la API REST de Despachatec-Backend, incluyendo los formatos exactos de request y response en JSON. El objetivo es servir como guía completa para el desarrollo frontend y la integración del sistema.

### URL Base
```
http://localhost:8080
```

### Autenticación
La mayoría de los endpoints requieren autenticación JWT. Para endpoints protegidos, incluir el header:
```
Authorization: Bearer <jwt-token>
```

**Importante:** Los tokens JWT incluyen información de roles del usuario, por lo que **es necesario hacer login nuevamente** después de actualizaciones del sistema para obtener un token con la estructura correcta.

### Roles de Usuario
- **ADMIN**: Acceso completo a todas las operaciones CRUD
- **USER**: Acceso de solo lectura a la mayoría de recursos

### Estructura del Token JWT
Los tokens JWT ahora incluyen los roles del usuario en el payload, eliminando la necesidad de consultar la base de datos en cada request. Esto mejora significativamente el rendimiento del sistema.

### Formato de Respuesta Estándar
Las respuestas siguen un formato consistente:
- **Endpoints de Autenticación**: Respuesta directa con datos del usuario
- **Endpoints de Clientes**: Respuesta directa con arrays o objetos
- **Endpoints de Empleados y Productos**: Respuesta directa con arrays o objetos
- **Endpoints de Pedidos y Reportes**: Respuesta envuelta en estructura estándar con `success`, `message`, `timestamp` y `data`

### Códigos de Estado HTTP
- **200**: Operación exitosa
- **201**: Recurso creado exitosamente
- **400**: Solicitud incorrecta (validación fallida, datos duplicados, reglas de negocio)
- **401**: No autorizado (token faltante o inválido)
- **403**: Prohibido (sin permisos para la operación)
- **404**: Recurso no encontrado
- **500**: Error interno del servidor

---

## 1. Autenticación y Registro (`/api/auth`)

### POST `/api/auth/login`
**Descripción:** Autentica un usuario usando username/email y contraseña, retorna un JWT.

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "usernameOrEmail": "usuario@example.com",
  "password": "password123"
}
```

**Validaciones Request:**
- `usernameOrEmail`: Obligatorio, no vacío
- `password`: Obligatorio, no vacío

**Response 200 - Autenticación exitosa:**
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbkBkZXNwYWNoYXRlYy5jb20iLCJyb2xlcyI6IlJPTEVfQURNSU4iLCJpYXQiOjE3NTExNDg0NTIsImV4cCI6MTc1MTc1MzI1Mn0...",
  "tokenType": "Bearer",
  "username": "admin",
  "nombre": "Administrador",
  "id": 1,
  "roles": ["ADMIN"]
}
```

**Nota:** El token JWT ahora incluye los roles del usuario en el payload (claim "roles"), lo que permite validación de permisos sin consultar la base de datos en cada request.

**Response 401 - Credenciales inválidas:**
```json
{
  "mensaje": "Credenciales inválidas"
}
```

---

### POST `/api/auth/register`
**Descripción:** Registra un nuevo usuario con rol USER por defecto.

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "nombre": "Juan Pérez",
  "username": "juanperez",
  "email": "juan@example.com",
  "password": "password123"
}
```

**Validaciones Request:**
- `nombre`: Obligatorio, no vacío
- `username`: Obligatorio, mínimo 4 caracteres
- `email`: Obligatorio, formato email válido
- `password`: Obligatorio, mínimo 6 caracteres

**Response 201 - Usuario creado exitosamente:**
```json
{
  "mensaje": "Usuario registrado correctamente",
  "usuario": {
    "id": 3,
    "nombre": "Juan Pérez",
    "username": "juanperez",
    "email": "juan@example.com",
    "roles": [
      {
        "id": 2,
        "nombre": "USER"
      }
    ]
  }
}
```

**Response 400 - Username ya existe:**
```json
{
  "mensaje": "El nombre de usuario ya está en uso"
}
```

**Response 400 - Email ya existe:**
```json
{
  "mensaje": "El email ya está en uso"
}
```

---

### POST `/api/auth/logout`
**Descripción:** Finaliza la sesión del usuario. El backend responde para que el frontend elimine el JWT del almacenamiento local.

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Response 200 - Logout exitoso:**
```json
{
  "mensaje": "Logout exitoso"
}
```

**Notas:**
- El backend no puede invalidar el JWT, pero este endpoint permite al frontend realizar el proceso de cierre de sesión de forma estándar y auditable.
- El frontend debe eliminar el token JWT del almacenamiento local al recibir una respuesta exitosa.

---

## 2. Gestión de Clientes (`/api/clientes`)

### GET `/api/clientes`
**Descripción:** Lista todos los clientes del sistema.

**Autorización:** ADMIN, USER

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Response 200:**
```json
[
  {
    "id": 1,
    "nombre": "María",
    "apellidos": "García López",
    "email": "maria.garcia@example.com",
    "telefono": "+1234567890",
    "direccion": "Calle Principal 123, Ciudad"
  }
]
```

---

### GET `/api/clientes/{id}`
**Descripción:** Obtiene un cliente específico por su ID.

**Autorización:** ADMIN, USER

**Response 200:**
```json
{
  "id": 1,
  "nombre": "María",
  "apellidos": "García López",
  "email": "maria.garcia@example.com",
  "telefono": "+1234567890",
  "direccion": "Calle Principal 123, Ciudad"
}
```

---

### POST `/api/clientes`
**Descripción:** Crea un nuevo cliente.

**Autorización:** ADMIN

**Request Body:**
```json
{
  "nombre": "Ana",
  "apellidos": "Martínez Silva",
  "email": "ana.martinez@example.com",
  "telefono": "+5544332211",
  "direccion": "Plaza Mayor 789, Ciudad"
}
```

**Validaciones:**
- `nombre`: Obligatorio, no vacío
- `apellidos`: Obligatorio, no vacío
- `email`: Opcional, formato email válido

**Response 201:**
```json
{
  "id": 3,
  "nombre": "Ana",
  "apellidos": "Martínez Silva",
  "email": "ana.martinez@example.com",
  "telefono": "+5544332211",
  "direccion": "Plaza Mayor 789, Ciudad"
}
```

---

### PUT `/api/clientes/{id}`
**Descripción:** Actualiza un cliente existente.

**Autorización:** ADMIN

**Response 200:** Mismo formato que POST

---

### DELETE `/api/clientes/{id}`
**Descripción:** Elimina un cliente.

**Autorización:** ADMIN

**Response 200:**
```json
{
  "deleted": true
}
```

---

### GET `/api/clientes/search?query=...`
**Descripción:** Busca clientes por nombre o apellidos.

**Autorización:** ADMIN, USER

**Response 200:** Array de clientes que coinciden

---

## 3. Gestión de Empleados (`/api/empleados`)

> **Nota:** Todos los endpoints requieren rol ADMIN.

### GET `/api/empleados`
**Descripción:** Lista todos los empleados.

**Response 200:**
```json
[
  {
    "id": 1,
    "nombre": "Ana",
    "apellidos": "González Martín",
    "dni": "12345678A",
    "telefono": "+1234567890",
    "direccion": "Calle Trabajo 123, Ciudad",
    "fechaContratacion": "2024-01-15T00:00:00.000Z",
    "cargo": "Cocinero",
    "salario": 2500.00,
    "usuarioId": 3
  }
]
```

---

### GET `/api/empleados/{id}`
**Descripción:** Obtiene un empleado por ID.

**Response 200:** Mismo formato que GET all

---

### GET `/api/empleados/cargo/{cargo}`
**Descripción:** Lista empleados por cargo.

**Response 200:** Array de empleados filtrados

---

### GET `/api/empleados/search?query=...`
**Descripción:** Busca empleados por nombre o apellidos.

**Response 200:** Array de empleados que coinciden

---

### POST `/api/empleados`
**Descripción:** Crea un nuevo empleado.

**Request Body:**
```json
{
  "nombre": "Miguel",
  "apellidos": "Rodríguez Silva",
  "dni": "11223344C",
  "telefono": "+1122334455",
  "direccion": "Plaza Central 111, Ciudad",
  "fechaContratacion": "2024-06-21T00:00:00.000Z",
  "cargo": "Mesero",
  "salario": 2300.00,
  "usuarioId": 5
}
```

**Validaciones:**
- `nombre`: Obligatorio, no vacío
- `apellidos`: Obligatorio, no vacío
- `dni`: Obligatorio, no vacío, único
- `fechaContratacion`: Opcional, fecha pasada o presente
- `salario`: Opcional, valor positivo

**Response 201:** Mismo formato que REQUEST

---

### PUT `/api/empleados/{id}`
**Descripción:** Actualiza un empleado.

**Response 200:** Mismo formato que POST

---

### DELETE `/api/empleados/{id}`
**Descripción:** Elimina un empleado.

**Response 200:**
```json
{
  "deleted": true
}
```

---

## 4. Gestión de Productos (`/api/productos`)

### GET `/api/productos`
**Descripción:** Lista todos los productos activos.

**Autorización:** No requerida

**Response 200:**
```json
[
  {
    "id": 1,
    "nombre": "Pizza Margarita",
    "descripcion": "Pizza clásica con tomate, mozzarella y albahaca fresca",
    "precio": 12.50,
    "imagenUrl": "https://example.com/images/pizza-margarita.jpg",
    "categoria": "Pizzas",
    "stockDisponible": 25,
    "estaActivo": true
  }
]
```

**Notas:**
- `estaActivo` es opcional en el request, por defecto true.

---

### GET `/api/productos/{id}`
**Descripción:** Obtiene un producto por ID.

**Response 200:** Mismo formato que GET all

---

### GET `/api/productos/categoria/{categoria}`
**Descripción:** Lista productos por categoría.

**Response 200:** Array de productos filtrados

---

### GET `/api/productos/search?query=...`
**Descripción:** Busca productos por nombre.

**Autorización:** No requerida

**Response 200:** Array de productos que coinciden

---

### GET `/api/productos/categorias`
**Descripción:** Lista todas las categorías de productos disponibles (solo productos activos).

**Autorización:** No requerida

**Response 200:**
```json
[
  "Pizzas",
  "Bebidas", 
  "Postres",
  "Ensaladas",
  "Pastas"
]
```

---

### POST `/api/productos`
**Descripción:** Crea un nuevo producto.

**Autorización:** ADMIN

**Request Body:**
```json
{
  "nombre": "Pizza Cuatro Quesos",
  "descripcion": "Pizza con mozzarella, gorgonzola, parmesano y ricotta",
  "precio": 16.50,
  "imagenUrl": "https://example.com/images/pizza-cuatro-quesos.jpg",
  "categoria": "Pizzas",
  "stockDisponible": 30,
  "estaActivo": true
}
```

**Validaciones:**
- `nombre`: Obligatorio, no vacío
- `precio`: Obligatorio, mayor o igual a cero
- `stockDisponible`: Opcional, mayor o igual a cero
- `estaActivo`: Opcional, por defecto true

**Response 201:** Mismo formato que REQUEST

---

### PUT `/api/productos/{id}`
**Descripción:** Actualiza un producto.

**Autorización:** ADMIN

**Response 200:** Mismo formato que POST

---

### DELETE `/api/productos/{id}`
**Descripción:** Marca producto como inactivo (eliminación lógica).

**Autorización:** ADMIN

**Response 200:**
```json
{
  "deleted": true
}
```

---

### GET `/api/productos/admin/all`
**Descripción:** Lista todos los productos (incluyendo inactivos).

**Autorización:** ADMIN

**Response 200:** Array con todos los productos

---

## 5. Gestión de Pedidos (`/api/pedidos`)

### GET `/api/pedidos`
**Descripción:** Lista todos los pedidos.

**Autorización:** ADMIN, USER

**Response 200:**
```json
{
  "success": true,
  "message": "Lista de pedidos obtenida",
  "timestamp": "2025-06-21T12:00:00Z",
  "data": [
    {
      "id": 1,
      "clienteId": 2,
      "empleadoId": 3,
      "fechaPedido": "2025-06-20T18:00:00Z",
      "fechaEntrega": "2025-06-20T19:00:00Z",
      "estado": "ENTREGADO",
      "total": 25.50,
      "detalles": [
        {
          "id": 10,
          "pedidoId": 1,
          "productoId": 5,
          "cantidad": 2,
          "precioUnitario": 10.50,
          "subtotal": 21.00,
          "nombreProducto": "Pizza Margarita",
          "descripcionProducto": "Pizza clásica con tomate y queso"
        }
      ]
    }
  ]
}
```

---

### GET `/api/pedidos/{id}`
**Descripción:** Obtiene un pedido por ID.

**Autorización:** ADMIN, USER

**Response 200:** Formato similar con un solo pedido en `data`

---

### GET `/api/pedidos/cliente/{clienteId}`
**Descripción:** Lista pedidos de un cliente.

**Autorización:** ADMIN, USER

**Response 200:** Array de pedidos del cliente

---

### GET `/api/pedidos/estado/{estado}`
**Descripción:** Lista pedidos por estado.

**Autorización:** ADMIN, USER

**Estados posibles:** `PENDIENTE`, `PREPARANDO`, `LISTO`, `ENTREGADO`, `CANCELADO`

**Response 200:** Array de pedidos filtrados

---

### POST `/api/pedidos`
**Descripción:** Crea un nuevo pedido.

**Autorización:** ADMIN, USER

**Request Body:**
```json
{
  "clienteId": 2,
  "empleadoId": 3,
  "detalles": [
    {
      "productoId": 5,
      "cantidad": 2
    },
    {
      "productoId": 8,
      "cantidad": 1
    }
  ]
}
```

**Validaciones:**
- `clienteId`: Obligatorio, debe existir
- `empleadoId`: Opcional, debe existir si se proporciona
- `detalles`: Obligatorio, mínimo un detalle
  - `productoId`: Obligatorio, debe existir
  - `cantidad`: Obligatorio, mínimo 1

**Response 201:** Pedido creado con detalles completos

---

### PUT `/api/pedidos/{id}/estado?estado=...`
**Descripción:** Actualiza estado de un pedido.

**Autorización:** ADMIN

**Estados:** `PENDIENTE`, `PREPARANDO`, `LISTO`, `ENTREGADO`, `CANCELADO`

**Response 200:** Pedido actualizado

---

### PUT `/api/pedidos/{id}/asignar-empleado/{empleadoId}`
**Descripción:** Asigna empleado a un pedido.

**Autorización:** ADMIN

**Response 200:** Pedido actualizado

---

### DELETE `/api/pedidos/{id}`
**Descripción:** Elimina pedido (solo si está PENDIENTE).

**Autorización:** ADMIN

**Response 200:**
```json
{
  "success": true,
  "message": "Pedido eliminado exitosamente",
  "timestamp": "2025-06-21T12:00:00Z",
  "data": null
}
```

---

## 6. Reportes y Analíticas (`/api/reportes`)

> **Nota:** Todos los endpoints requieren rol ADMIN.

### GET `/api/reportes/ventas-por-periodo?fechaInicio=YYYY-MM-DD&fechaFin=YYYY-MM-DD`
**Descripción:** Estadísticas de ventas en un periodo.

**Response 200:**
```json
{
  "success": true,
  "message": "Estadísticas de ventas generadas con éxito",
  "timestamp": "2025-06-21T12:00:00Z",
  "data": {
    "ventasTotales": 1500.50,
    "numeroPedidos": 45,
    "pedidosPorEstado": {
      "PENDIENTE": 5,
      "PREPARANDO": 10,
      "LISTO": 5,
      "ENTREGADO": 30,
      "CANCELADO": 0
    },
    "fechaInicio": "2025-06-01",
    "fechaFin": "2025-06-21"
  }
}
```

---

### GET `/api/reportes/productos-mas-vendidos?fechaInicio=YYYY-MM-DD&fechaFin=YYYY-MM-DD&limite=10`
**Descripción:** Productos más vendidos (periodo opcional).

**Parámetros:**
- `fechaInicio`: Opcional, formato YYYY-MM-DD
- `fechaFin`: Opcional, formato YYYY-MM-DD
- `limite`: Opcional, por defecto 10

**Response 200:**
```json
{
  "success": true,
  "message": "Productos más vendidos generados con éxito",
  "timestamp": "2025-06-21T12:00:00Z",
  "data": [
    {
      "id": 1,
      "nombre": "Pizza Margarita",
      "categoria": "Pizzas",
      "cantidadVendida": 50,
      "ingresos": 525.00
    }
  ]
}
```

---

### GET `/api/reportes/clientes-frecuentes?limite=10`
**Descripción:** Clientes con más pedidos y gasto total.

**Parámetros:**
- `limite`: Opcional, por defecto 10

**Response 200:**
```json
{
  "success": true,
  "message": "Clientes frecuentes generados con éxito",
  "timestamp": "2025-06-21T12:00:00Z",
  "data": [
    {
      "id": 2,
      "nombre": "Juan Pérez",
      "email": "juan@example.com",
      "numeroPedidos": 12,
      "gastoTotal": 300.50
    }
  ]
}
```

---

### GET `/api/reportes/resumen-inventario`
**Descripción:** Resumen de inventario y alertas de stock.

**Response 200:**
```json
{
  "success": true,
  "message": "Resumen de inventario generado con éxito",
  "timestamp": "2025-06-21T12:00:00Z",
  "data": {
    "totalProductos": 20,
    "productosAgotados": 2,
    "productosBajoStock": 3,
    "productosPorCategoria": {
      "Pizzas": 10,
      "Hamburguesas": 5,
      "Bebidas": 5
    },
    "alertasStock": [
      {
        "id": 5,
        "nombre": "Pizza Cuatro Estaciones",
        "categoria": "Pizzas",
        "stockActual": 0,
        "estado": "AGOTADO"
      }
    ]
  }
}
```

---

## Flujo de Trabajo Recomendado para Frontend

### 1. Autenticación
1. Usuario hace login con `POST /api/auth/login`
2. Guardar token JWT en localStorage/sessionStorage
3. **Importante:** El token JWT contiene los roles del usuario, asegurando autorización eficiente
4. Incluir token en header Authorization de todas las requests
5. **Nota:** Después de actualizaciones del sistema, es necesario hacer login nuevamente para obtener tokens con la estructura actualizada

### 2. Gestión de Datos
1. **Clientes**: CRUD completo para ADMIN, solo lectura para USER
2. **Empleados**: Solo ADMIN puede gestionar
3. **Productos**: Lectura pública, CRUD para ADMIN
4. **Pedidos**: CRUD con validaciones de negocio
5. **Reportes**: Solo ADMIN puede acceder

### 3. Manejo de Errores
- Verificar siempre el código de estado HTTP
- Para endpoints con formato estándar, verificar `success: true/false`
- Mostrar mensajes de error apropiados al usuario
- Manejar tokens expirados (401) redirigiendo al login
- **Nuevo:** Si hay error 403 (Forbidden) después de login exitoso, hacer logout y login nuevamente para obtener token actualizado

### 4. Estados de Carga
- Mostrar indicadores durante requests HTTP
- Manejar timeouts de red
- Implementar retry para operaciones críticas

---

## Notas Adicionales

### Seguridad
- JWT tokens tienen expiración configurada
- JWT tokens incluyen roles del usuario para autorización stateless
- Endpoints protegidos validan roles automáticamente desde el token
- Validaciones de entrada en todos los endpoints
- **Recomendación:** Implementar refresh de tokens cuando estén próximos a expirar

### Rendimiento
- JWT tokens con roles embebidos eliminan consultas de base de datos para autorización
- Endpoints de listado pueden retornar grandes volúmenes
- Considerar paginación para listas extensas
- Cache local para datos que cambian poco
- **Mejora:** Validación de roles ahora es O(1) en lugar de consulta DB

### Mantenimiento
- Documentación sincronizada con implementación
- Versionado de API para cambios futuros
- Logs detallados para debugging

---

## Changelog

### v1.2 (2025-06-28)
**Mejoras en Sistema de Autenticación JWT:**
- ✅ Los tokens JWT ahora incluyen roles del usuario en el payload
- ✅ Eliminadas consultas a base de datos para validación de roles en cada request
- ✅ Mejor rendimiento: autorización ahora es O(1)
- ✅ Mantiene compatibilidad con estructura de respuesta existente
- ⚠️ **Acción requerida:** Usuarios deben hacer login nuevamente para obtener tokens actualizados

### v1.1 (2025-06-21)
- Documentación revisada y corregida tras validación exhaustiva
- Formatos de request/response validados
- Ejemplos actualizados

---

**Documento generado:** 2025-06-28  
**Versión:** 1.2  
**Estado:** Actualizado con mejoras en sistema de autenticación JWT  
**Cambios principales:** Tokens JWT ahora incluyen roles para mejor rendimiento y seguridad
