-- Insertar roles
INSERT INTO roles (nombre) VALUES ('ADMIN') ON DUPLICATE KEY UPDATE nombre = nombre;
INSERT INTO roles (nombre) VALUES ('USER') ON DUPLICATE KEY UPDATE nombre = nombre;

-- Insertar usuario administrador (password: admin123)
INSERT INTO usuarios (nombre, username, email, password)
VALUES ('Administrador', 'admin', 'admin@despachatec.com', '$2a$10$TfVSHGqMXFepVnuT.3QF6eSwp7KK2o5Z1OlFyPqREPjrzQkFGz9LK')
ON DUPLICATE KEY UPDATE username = username;

-- Asignar rol de administrador al usuario admin
INSERT INTO usuario_roles (usuario_id, rol_id)
SELECT u.id, r.id FROM usuarios u, roles r
WHERE u.username = 'admin' AND r.nombre = 'ADMIN'
ON DUPLICATE KEY UPDATE usuario_id = usuario_id;

-- Insertar productos de ejemplo
INSERT INTO productos (nombre, descripcion, precio, imagen_url, categoria, stock_disponible, esta_activo)
VALUES ('Hamburguesa Clásica', 'Hamburguesa con carne, lechuga, tomate y queso', 8.99, 'hamburguesa_clasica.jpg', 'HAMBURGUESAS', 50, true),
       ('Pizza Margherita', 'Pizza con salsa de tomate, mozzarella y albahaca', 12.50, 'pizza_margherita.jpg', 'PIZZAS', 30, true),
       ('Ensalada César', 'Lechuga romana, crutones, queso parmesano y aderezo César', 6.99, 'ensalada_cesar.jpg', 'ENSALADAS', 25, true),
       ('Refresco Cola', 'Refresco de cola 355ml', 2.50, 'refresco_cola.jpg', 'BEBIDAS', 100, true),
       ('Papas Fritas', 'Porción grande de papas fritas', 3.99, 'papas_fritas.jpg', 'ACOMPAÑAMIENTOS', 40, true),
       ('Helado de Vainilla', 'Copa de helado de vainilla con jarabe de chocolate', 4.50, 'helado_vainilla.jpg', 'POSTRES', 20, true),
       ('Agua Mineral', 'Botella de agua mineral 500ml', 1.99, 'agua_mineral.jpg', 'BEBIDAS', 150, true),
       ('Tacos de Carne', 'Orden de 3 tacos de carne asada', 7.99, 'tacos_carne.jpg', 'TACOS', 35, true),
       ('Cerveza Nacional', 'Cerveza nacional 355ml', 3.50, 'cerveza_nacional.jpg', 'BEBIDAS_ALCOHOLICAS', 80, true),
       ('Nachos con Queso', 'Nachos con queso fundido y guacamole', 5.99, 'nachos_queso.jpg', 'ENTRADAS', 25, true);

-- Insertar clientes de ejemplo
INSERT INTO clientes (nombre, apellidos, email, telefono, direccion)
VALUES ('Juan', 'Pérez González', 'juan.perez@email.com', '555-123-4567', 'Calle Principal 123'),
       ('María', 'López Rodríguez', 'maria.lopez@email.com', '555-987-6543', 'Avenida Central 456'),
       ('Carlos', 'Sánchez Martínez', 'carlos.sanchez@email.com', '555-456-7890', 'Plaza Mayor 789'),
       ('Ana', 'Martínez García', 'ana.martinez@email.com', '555-234-5678', 'Calle Secundaria 321'),
       ('Pedro', 'González Fernández', 'pedro.gonzalez@email.com', '555-876-5432', 'Avenida Norte 654');

-- Insertar empleados de ejemplo
INSERT INTO empleados (nombre, apellidos, dni, telefono, direccion, fecha_contratacion, cargo, salario)
VALUES ('Luis', 'Ramírez Vega', '12345678A', '555-111-2222', 'Calle Empleados 111', '2023-01-15', 'COCINERO', 1800.00),
       ('Laura', 'Fernández Ruiz', '23456789B', '555-333-4444', 'Avenida Trabajo 222', '2023-02-10', 'MESERO', 1500.00),
       ('Roberto', 'Torres Cruz', '34567890C', '555-555-6666', 'Plaza Laboral 333', '2023-03-05', 'CAJERO', 1600.00),
       ('Carmen', 'Ruiz Castro', '45678901D', '555-777-8888', 'Calle Personal 444', '2023-04-20', 'REPARTIDOR', 1400.00),
       ('Javier', 'Castro Luna', '56789012E', '555-999-0000', 'Avenida Staff 555', '2023-05-15', 'GERENTE', 2200.00);
