package com.pollution.gateway.exception;

/**
 * Excepción personalizada para representar un recurso no encontrado (HTTP 404).
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
