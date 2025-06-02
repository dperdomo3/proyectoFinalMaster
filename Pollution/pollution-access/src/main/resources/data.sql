-- Crear tabla estacion
CREATE TABLE IF NOT EXISTS estacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    direccion VARCHAR(255),
    latitud DOUBLE,
    longitud DOUBLE
);

-- Insertar datos iniciales
INSERT INTO estacion (direccion, latitud, longitud) VALUES
('Calle Viento', 39.4705, -0.3760),
('Av. Brisa', 29.4715, 0.3775),
('Plaza Aire', 9.4690, 4.3750),
('Ronda del Oxígeno', 19.4688, 6.3781),
('Calle Cielo', 3.4722, -7.3742);
