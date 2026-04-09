-- ================================================
-- BIKESTORE FASE 2 - DATOS INICIALES
-- ================================================

-- Insertar configuración global única del sistema (solo si no existe)
INSERT INTO configuracion (nombre_tienda, umbral_aprobacion_venta, umbral_aprobacion_pedido, horario_inicio, horario_fin)
SELECT 'BikeStore', 2000000.00, 5000000.00, '08:00:00', '18:00:00'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM configuracion LIMIT 1);
