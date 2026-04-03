package com.bike.shop.exception;

// Se lanza cuando no se encuentra un recurso en la base de datos (HTTP 404)
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
