-- ================================================
-- BIKESTORE FASE 2 - ALTERACIONES A TABLAS EXISTENTES
-- Ejecutar una sola vez. Si el campo ya existe, comentar la linea.
-- ================================================

-- Agregar stock_minimo a bicicleta
-- NOTA: MySQL Railway no soporta ADD COLUMN IF NOT EXISTS en versiones antiguas.
-- Si el campo ya existe en la BD, comentar esta linea antes de ejecutar.
ALTER TABLE bicicleta ADD COLUMN stock_minimo INT DEFAULT 5;

-- ------------------------------------------------
-- Valores validos del campo estado en tabla venta:
--   completada           -> venta procesada normalmente
--   devuelta             -> venta cancelada/devuelta
--   pendiente_aprobacion -> supera el umbral, esperando aprobacion de ADMIN
--   rechazada            -> venta pendiente rechazada por ADMIN
-- El campo ya existe con length 20, ninguna alteracion necesaria.
-- ------------------------------------------------
