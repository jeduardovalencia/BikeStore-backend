package com.bike.shop.exception;

// Se lanza cuando los datos enviados no cumplen las reglas de negocio (HTTP 400)
public class ValidacionException extends RuntimeException {
    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}
