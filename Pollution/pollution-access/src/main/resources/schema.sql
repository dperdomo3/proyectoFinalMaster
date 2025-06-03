-- Crear tabla estacion
CREATE TABLE IF NOT EXISTS estacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    direccion VARCHAR(255),
    latitud DOUBLE,
    longitud DOUBLE
);