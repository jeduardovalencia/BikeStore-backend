-- ================================================
-- BIKESTORE FASE 2 - NUEVAS TABLAS
-- ================================================

-- Tabla: configuracion (singleton global del sistema)
CREATE TABLE IF NOT EXISTS configuracion (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre_tienda VARCHAR(100) NOT NULL DEFAULT 'BikeStore',
    umbral_aprobacion_venta DECIMAL(15,2) NOT NULL DEFAULT 2000000.00,
    umbral_aprobacion_pedido DECIMAL(15,2) NOT NULL DEFAULT 5000000.00,
    horario_inicio TIME DEFAULT '08:00:00',
    horario_fin TIME DEFAULT '18:00:00'
);

-- Tabla: aprobacion_venta (flujo de aprobación por monto)
-- detalles_json almacena los items de la venta pendiente hasta su aprobación
CREATE TABLE IF NOT EXISTS aprobacion_venta (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_venta INT NOT NULL,
    id_usuario_solicitante INT NOT NULL,
    id_usuario_aprobador INT NULL,
    fecha_solicitud DATETIME NOT NULL,
    fecha_resolucion DATETIME NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'pendiente',  -- pendiente / aprobada / rechazada
    observacion TEXT NULL,
    monto_total DECIMAL(15,2) NOT NULL,
    detalles_json TEXT NULL,
    FOREIGN KEY (id_venta) REFERENCES venta(id),
    FOREIGN KEY (id_usuario_solicitante) REFERENCES usuario(id),
    FOREIGN KEY (id_usuario_aprobador) REFERENCES usuario(id)
);

-- Tabla: notificacion
CREATE TABLE IF NOT EXISTS notificacion (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_usuario_destino INT NOT NULL,
    titulo VARCHAR(100) NOT NULL,
    mensaje TEXT NOT NULL,
    tipo VARCHAR(20) NOT NULL DEFAULT 'info',  -- info / warning / danger
    leida BOOLEAN NOT NULL DEFAULT false,
    fecha DATETIME NOT NULL,
    url_accion VARCHAR(255) NULL,
    FOREIGN KEY (id_usuario_destino) REFERENCES usuario(id)
);

-- Tabla: auditoria
CREATE TABLE IF NOT EXISTS auditoria (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_usuario INT NULL,
    accion VARCHAR(50) NOT NULL,
    modulo VARCHAR(50) NOT NULL,
    descripcion TEXT NULL,
    fecha DATETIME NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id)
);
