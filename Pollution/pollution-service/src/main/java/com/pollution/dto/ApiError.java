package com.pollution.dto;

import java.time.LocalDateTime;

/**
 * Representa un error de API con código de estado, mensaje y timestamp.
 */
public class ApiError {
    private int status;
    private String message;
    private LocalDateTime timestamp;

    public ApiError(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now(); // Timestamp automático
    }

    // Getters y setters
    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
