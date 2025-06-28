-- ============================================================
-- SCRIPT DE LIMPIEZA DE DATOS DE PRUEBA
-- ============================================================
-- Usar cuando se necesite limpiar datos de ejemplo
-- CUIDADO: Solo ejecutar en entornos de desarrollo/testing
-- ============================================================

-- Limpiar datos en orden correcto (respetando foreign keys)
DELETE FROM detalle_pedidos;
DELETE FROM pedidos;
DELETE FROM productos;
DELETE FROM empleados;
DELETE FROM clientes;
DELETE FROM usuario_roles;
DELETE FROM usuarios WHERE username != 'admin';

-- Resetear auto-increment (opcional)
ALTER TABLE detalle_pedidos AUTO_INCREMENT = 1;
ALTER TABLE pedidos AUTO_INCREMENT = 1;
ALTER TABLE productos AUTO_INCREMENT = 1;
ALTER TABLE empleados AUTO_INCREMENT = 1;
ALTER TABLE clientes AUTO_INCREMENT = 1;

SELECT 'Datos de prueba eliminados correctamente' AS resultado;
