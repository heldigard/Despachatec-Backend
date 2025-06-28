# Despachatec-Backend

## Overview
Despachatec-Backend is a RESTful API built with Spring Boot for managing restaurant operations. It provides secure endpoints for user authentication, client and employee management, product and order handling, and business reporting. The backend is designed to be robust, extensible, and secure, supporting role-based access control and JWT authentication.

## Purpose
This project serves as the backend for the Despachatec restaurant management system. It centralizes business logic, data persistence, and security, enabling efficient management of users, orders, products, and reports.

## Main Features
- **User Authentication & Authorization**: JWT-based login, registration, and role-based access (ADMIN, USER).
- **Client Management**: CRUD operations for restaurant clients.
- **Employee Management**: CRUD operations for employees, with role restrictions.
- **Product Management**: CRUD operations for products, including category and stock management.
- **Order Management**: Creation, update, assignment, and deletion of orders, with stock validation and order state transitions.
- **Reporting**: Endpoints for business analytics, accessible to admins.
- **API Response Standardization**: Consistent response format via `ApiResponseBuilder`.

## Technologies Used
- Java 11+
- Spring Boot (Web, Data JPA, Security)
- MySQL
- JWT (JSON Web Token) for authentication
- Lombok (for model boilerplate)
- Maven (build tool)

## Architecture & Structure
- **Controllers**: Handle HTTP requests and responses (e.g., `AuthController`, `ClienteController`, `EmpleadoController`, `PedidoController`, `ProductoController`, `ReporteController`).
- **Models**: JPA entities representing domain objects (`Usuario`, `Rol`, `Empleado`, `Cliente`, `Pedido`, `Producto`, `DetallePedido`).
- **Repositories**: Spring Data JPA interfaces for data access.
- **Security**: JWT authentication, password encryption, and role-based access control.
- **Utils**: Helper classes for API responses and other utilities.

### High-Level Flow
1. **Authentication**: Users authenticate via `/api/auth/login`, receiving a JWT token.
2. **Authorization**: Endpoints are protected by roles (ADMIN, USER) using Spring Security annotations.
3. **Business Logic**: Controllers delegate to repositories and models for CRUD and business operations.
4. **Database**: MySQL stores all persistent data. Initial data is loaded from `data.sql`.

## Security
- **JWT Authentication**: Stateless authentication using signed tokens.
- **Role-Based Access**: Endpoints are protected using `@PreAuthorize` annotations.
- **Password Encryption**: Passwords are stored hashed with BCrypt.
- **CORS**: Configured to allow cross-origin requests.

## Database Initialization
- **Schema**: Managed by JPA/Hibernate (`ddl-auto=update`).
- **Initial Data**: `data.sql` inserts default roles (ADMIN, USER), an admin user, and sample products.
- **User-Role Assignment**: Admin user is assigned the ADMIN role at startup.

## Main API Endpoints
### AuthController (`/api/auth`)
- `POST /login`: Authenticate user, returns JWT.
- `POST /register`: Register new user.

### ClienteController (`/api/clientes`)
- `GET /`: List all clients (ADMIN, USER).
- `GET /{id}`: Get client by ID (ADMIN, USER).
- `POST /`: Create client (ADMIN).

### EmpleadoController (`/api/empleados`)
- `GET /`: List all employees (ADMIN).
- `GET /{id}`: Get employee by ID (ADMIN).
- `GET /cargo/{cargo}`: List employees by role (ADMIN).

### ProductoController (`/api/productos`)
- `GET /`: List all products.
- `GET /{id}`: Get product by ID.
- `GET /categoria/{categoria}`: List products by category.
- `GET /search?query=...`: Search products by name.
- `POST /`: Create product (ADMIN).

### PedidoController (`/api/pedidos`)
- `GET /`: List all orders (ADMIN, USER).
- `GET /{id}`: Get order by ID (ADMIN, USER).
- `GET /cliente/{clienteId}`: Orders by client (ADMIN, USER).
- `GET /estado/{estado}`: Orders by status (ADMIN, USER).
- `POST /`: Create order (ADMIN, USER).
- `PUT /{id}/estado?estado=...`: Update order status (ADMIN).
- `PUT /{id}/asignar-empleado/{empleadoId}`: Assign employee to order (ADMIN).
- `DELETE /{id}`: Delete order (ADMIN).

### ReporteController (`/api/reportes`)
- **All endpoints require ADMIN role.**
- Provides business analytics and reporting (see controller for details).

## Setup & Running
1. **Prerequisites**:
   - Java 11 or higher
   - Maven
   - MySQL (running, with user/password as in `application.properties`)
2. **Clone the repository**
3. **Configure Database**:
   - Edit `src/main/resources/application.properties` if needed.
   - Default DB: `despachatec` on localhost, user: `root`, password: `root`.
4. **Build and Run**:
   - `mvn clean install`
   - `mvn spring-boot:run`
5. **API Access**:
   - Server runs on `http://localhost:8080/`
   - Use tools like Postman to interact with endpoints.

## Extending & Contributing
- Add new features by creating new controllers, models, and repositories as needed.
- Follow existing code style and structure.
- Ensure new endpoints are secured appropriately.
- Update `data.sql` for new initial data if required.

## Further Documentation
- DTOs and model classes are documented in code.
- See controller classes for endpoint details and security annotations.
- Utility classes like `ApiResponseBuilder` standardize API responses.

## License
This project is proprietary. Contact the author for licensing information.

## Datos Iniciales

### Inicialización Automática
Al iniciar la aplicación por primera vez, se crearán automáticamente:
- Roles básicos: ADMIN, USER
- Usuario administrador:
  - Username: `admin`
  - Email: `admin@despachatec.com` 
  - Password: `admin123`
  - Rol: ADMIN

### Datos de Prueba (Opcional)
Para agregar datos de ejemplo para desarrollo/testing:
```sql
-- Ejecutar manualmente en la base de datos
source src/main/resources/sample-data.sql;
```

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── despachatec/
│   │           ├── controller/      # Controladores REST
│   │           ├── model/           # Modelos JPA
│   │           ├── repository/      # Repositorios Spring Data
│   │           ├── security/        # Configuración de seguridad
│   │           ├── service/         # Lógica de negocio
│   │           └── DespachatecBackendApplication.java
│   └── resources/
│       ├── sample-data.sql    # Datos de ejemplo (ejecutar manualmente)
│       ├── cleanup-data.sql   # Script de limpieza de datos
│       └── application.properties
```
