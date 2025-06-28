-- ============================================================
-- DATOS DE EJEMPLO PARA DESARROLLO Y TESTING
-- ============================================================
-- Ejecutar manualmente cuando se necesiten datos de prueba
-- ============================================================

-- Verificar que existan las tablas necesarias
-- Si no existen roles básicos, crearlos primero
INSERT IGNORE INTO roles (nombre) VALUES ('ROLE_ADMIN');
INSERT IGNORE INTO roles (nombre) VALUES ('ROLE_USER');

-- Verificar usuario admin existe

-- Insertar clientes de ejemplo
INSERT INTO clientes (nombre, apellidos, email, telefono, direccion) VALUES
('María', 'García López', 'maria.garcia@example.com', '+1234567890', 'Calle Principal 123, Ciudad'),
('Carlos', 'Rodríguez Sánchez', 'carlos.rodriguez@example.com', '+9876543210', 'Avenida Central 456, Ciudad'),
('Ana', 'Martínez Silva', 'ana.martinez@example.com', '+5544332211', 'Plaza Mayor 789, Ciudad');

-- Insertar empleados de ejemplo
INSERT INTO empleados (nombre, apellidos, dni, telefono, direccion, fecha_contratacion, cargo, salario, usuario_id) VALUES
('Ana', 'González Martín', '12345678A', '+1234567890', 'Calle Trabajo 123, Ciudad', '2024-01-15', 'Cocinero', 2500.00, NULL),
('Luis', 'Fernández Ruiz', '87654321B', '+0987654321', 'Plaza Laboral 456, Ciudad', '2024-02-20', 'Mesero', 2200.00, NULL),
('Carmen', 'López Torres', '11223344C', '+1122334455', 'Avenida Trabajo 789, Ciudad', '2024-03-10', 'Cajero', 2100.00, NULL);

-- Insertar productos de ejemplo
INSERT INTO productos (nombre, descripcion, precio, imagen_url, categoria, stock_disponible, esta_activo) VALUES
-- Pizzas
('Pizza Margarita', 'Pizza clásica con tomate, mozzarella y albahaca fresca', 12.50, 'https://example.com/images/pizza-margarita.jpg', 'Pizzas', 25, 1),
('Pizza Pepperoni', 'Pizza con pepperoni y queso mozzarella', 14.00, 'https://example.com/images/pizza-pepperoni.jpg', 'Pizzas', 20, 1),
('Pizza Cuatro Quesos', 'Pizza con mozzarella, gorgonzola, parmesano y ricotta', 16.50, 'https://example.com/images/pizza-cuatro-quesos.jpg', 'Pizzas', 15, 1),
('Pizza Vegetariana', 'Pizza con verduras frescas y queso', 13.50, 'https://example.com/images/pizza-vegetariana.jpg', 'Pizzas', 18, 1),
('Pizza Hawaiana', 'Pizza con jamón, piña y queso', 15.00, 'https://example.com/images/pizza-hawaiana.jpg', 'Pizzas', 22, 1),
-- Bebidas
('Coca Cola', 'Bebida gaseosa 350ml', 2.50, 'https://example.com/images/coca-cola.jpg', 'Bebidas', 100, 1),
('Agua Mineral', 'Agua mineral natural 500ml', 1.50, 'https://example.com/images/agua-mineral.jpg', 'Bebidas', 150, 1),
('Cerveza Artesanal', 'Cerveza artesanal local 330ml', 4.00, 'https://example.com/images/cerveza-artesanal.jpg', 'Bebidas', 50, 1),
('Jugo de Naranja', 'Jugo natural de naranja 250ml', 3.00, 'https://example.com/images/jugo-naranja.jpg', 'Bebidas', 80, 1),
-- Ensaladas
('Ensalada César', 'Ensalada con lechuga, pollo, crutones y aderezo césar', 8.50, 'https://example.com/images/ensalada-cesar.jpg', 'Ensaladas', 30, 1),
('Ensalada Mixta', 'Ensalada con lechuga, tomate, cebolla y aceitunas', 6.00, 'https://example.com/images/ensalada-mixta.jpg', 'Ensaladas', 35, 1),
-- Postres
('Tiramisu', 'Postre italiano clásico', 6.50, 'https://example.com/images/tiramisu.jpg', 'Postres', 12, 1),
('Cheesecake', 'Tarta de queso con frutos rojos', 7.00, 'https://example.com/images/cheesecake.jpg', 'Postres', 10, 1),
-- Pastas
('Spaghetti Carbonara', 'Pasta con salsa carbonara y panceta', 11.50, 'https://example.com/images/carbonara.jpg', 'Pastas', 20, 1),
('Lasaña Boloñesa', 'Lasaña con carne y salsa boloñesa', 13.00, 'https://example.com/images/lasana.jpg', 'Pastas', 15, 1);

-- Insertar pedidos de ejemplo
INSERT INTO pedidos (cliente_id, empleado_id, fecha_pedido, fecha_entrega, estado, total) VALUES
(1, 1, '2024-06-20 18:00:00', '2024-06-20 19:00:00', 'ENTREGADO', 27.50),
(2, 2, '2024-06-21 12:30:00', '2024-06-21 13:30:00', 'ENTREGADO', 31.00),
(3, 1, '2024-06-21 14:00:00', NULL, 'PREPARANDO', 24.00),
(1, 3, '2024-06-21 15:30:00', NULL, 'PENDIENTE', 19.50);

-- Insertar detalles de pedidos
INSERT INTO detalle_pedidos (pedido_id, producto_id, cantidad, precio_unitario, subtotal) VALUES
-- Pedido 1 (María García - ENTREGADO)
(1, 1, 2, 12.50, 25.00),  -- 2x Pizza Margarita
(1, 6, 1, 2.50, 2.50),    -- 1x Coca Cola
-- Pedido 2 (Carlos Rodríguez - ENTREGADO)
(2, 3, 1, 16.50, 16.50),  -- 1x Pizza Cuatro Quesos
(2, 10, 1, 8.50, 8.50),   -- 1x Ensalada César
(2, 7, 4, 1.50, 6.00),    -- 4x Agua Mineral
-- Pedido 3 (Ana Martínez - PREPARANDO)
(3, 2, 1, 14.00, 14.00),  -- 1x Pizza Pepperoni
(3, 8, 1, 4.00, 4.00),    -- 1x Cerveza Artesanal
(3, 7, 4, 1.50, 6.00),    -- 4x Agua Mineral
-- Pedido 4 (María García - PENDIENTE)
(4, 14, 1, 11.50, 11.50), -- 1x Spaghetti Carbonara
(4, 9, 1, 3.00, 3.00),    -- 1x Jugo de Naranja
(4, 12, 1, 6.50, 6.50);   -- 1x Tiramisu

-- Verificar datos insertados
SELECT 'Clientes insertados:' AS info, COUNT(*) AS cantidad FROM clientes;
SELECT 'Empleados insertados:' AS info, COUNT(*) AS cantidad FROM empleados;
SELECT 'Productos insertados:' AS info, COUNT(*) AS cantidad FROM productos;
SELECT 'Pedidos insertados:' AS info, COUNT(*) AS cantidad FROM pedidos;
SELECT 'Detalles de pedidos insertados:' AS info, COUNT(*) AS cantidad FROM detalle_pedidos;
