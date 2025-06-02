package com.data.pollutionaccess.exception;

// Excepción personalizada para recursos no encontrados
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }
}
