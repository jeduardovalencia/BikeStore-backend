package com.bike.shop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 - Recurso no encontrado
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleNoEncontrado(RecursoNoEncontradoException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // 400 - Error de validación de negocio
    @ExceptionHandler(ValidacionException.class)
    public ResponseEntity<Map<String, Object>> handleValidacion(ValidacionException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleIntegridad(
            org.springframework.dao.DataIntegrityViolationException ex) {
        String mensaje = "Error de integridad en la base de datos";
        // Buscar el mensaje en toda la cadena de excepciones
        Throwable t = ex;
        while (t != null) {
            if (t.getMessage() != null && t.getMessage().contains("No hay suficiente stock")) {
                mensaje = "No hay suficiente stock para esta venta";
                return buildResponse(HttpStatus.BAD_REQUEST, mensaje);
            }
            t = t.getCause();
        }
        return buildResponse(HttpStatus.BAD_REQUEST, mensaje);
    }

    // 500 - Error inesperado
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        // Buscar mensaje del trigger en toda la cadena
        Throwable t = ex;
        while (t != null) {
            if (t.getMessage() != null &&
                    t.getMessage().contains("No hay suficiente stock")) {
                return buildResponse(HttpStatus.BAD_REQUEST,
                        "No hay suficiente stock para esta venta");
            }
            t = t.getCause();
        }
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor: " + ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String mensaje) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("mensaje", mensaje);
        body.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(status).body(body);
    }


}
