USE tienda_bicicletas;

DELIMITER //

-- Validar stock
CREATE TRIGGER check_stock
BEFORE INSERT ON detalle_venta
FOR EACH ROW
BEGIN
    DECLARE stock_actual INT;

    SELECT cantidad INTO stock_actual
    FROM bicicleta
    WHERE codigo = NEW.codigo_bicicleta;

    IF stock_actual < NEW.cantidad THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: No hay suficiente stock';
    END IF;
END //

-- Precio automático
CREATE TRIGGER precio_automatico
BEFORE INSERT ON detalle_venta
FOR EACH ROW
BEGIN
    DECLARE precio_bici DECIMAL(10,2);

    IF NEW.precio_unitario IS NULL OR NEW.precio_unitario = 0 THEN
        SELECT precio_venta INTO precio_bici
        FROM bicicleta
        WHERE codigo = NEW.codigo_bicicleta;

        SET NEW.precio_unitario = precio_bici;
    END IF;
END //

-- Subtotal
CREATE TRIGGER calc_subtotal
BEFORE INSERT ON detalle_venta
FOR EACH ROW
BEGIN
    SET NEW.subtotal = NEW.cantidad * NEW.precio_unitario;
END //

-- Descontar stock
CREATE TRIGGER update_stock
AFTER INSERT ON detalle_venta
FOR EACH ROW
BEGIN
    UPDATE bicicleta
    SET cantidad = cantidad - NEW.cantidad
    WHERE codigo = NEW.codigo_bicicleta;
END //

-- Total venta
CREATE TRIGGER update_venta_total
AFTER INSERT ON detalle_venta
FOR EACH ROW
BEGIN
    UPDATE venta
    SET total = (
        SELECT COALESCE(SUM(subtotal), 0)
        FROM detalle_venta
        WHERE id_venta = NEW.id_venta
    )
    WHERE id = NEW.id_venta;
END //

-- Pedido recibido suma stock
CREATE TRIGGER actualizar_stock_pedido
AFTER UPDATE ON pedido
FOR EACH ROW
BEGIN
    IF NEW.estado = 'recibido' AND OLD.estado = 'pendiente' THEN
        UPDATE bicicleta b
        INNER JOIN detalle_pedido dp ON dp.codigo_bicicleta = b.codigo
        SET b.cantidad = b.cantidad + dp.cantidad
        WHERE dp.id_pedido = NEW.id;
    END IF;
END //

-- Cancelación devuelve stock
CREATE TRIGGER devolver_stock_cancelacion
AFTER UPDATE ON venta
FOR EACH ROW
BEGIN
    IF NEW.estado = 'devuelta' AND OLD.estado = 'completada' THEN
        UPDATE bicicleta b
        INNER JOIN detalle_venta dv ON dv.codigo_bicicleta = b.codigo
        SET b.cantidad = b.cantidad + dv.cantidad
        WHERE dv.id_venta = NEW.id;
    END IF;
END //

DELIMITER ;