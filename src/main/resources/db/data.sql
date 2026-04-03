USE tienda_bicicletas;

-- ROLES
INSERT INTO rol (nombre) VALUES ('ADMIN');
INSERT INTO rol (nombre) VALUES ('EMPLEADO');

-- USUARIO ADMIN (password: admin123)
INSERT INTO usuario (username, password, nombre, email, estado, rol_id)
VALUES (
    'admin',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'Administrador',
    'admin@bikestore.com',
    true,
    1
);