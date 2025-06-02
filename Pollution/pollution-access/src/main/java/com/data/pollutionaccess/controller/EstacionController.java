package com.data.pollutionaccess.controller;

import com.data.pollutionaccess.model.Estacion;
import com.data.pollutionaccess.repository.EstacionRepository;
import com.data.pollutionaccess.exception.ResourceNotFoundException;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/estacion")
public class EstacionController {

    private final EstacionRepository estacionRepository;

    public EstacionController(EstacionRepository estacionRepository) {
        this.estacionRepository = estacionRepository;
    }

    //  Obtener todas las estaciones
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(estacionRepository.findAll());
    }

    //  Crear una nueva estación
    @PostMapping
    public ResponseEntity<Estacion> create(@RequestBody Estacion estacion) {
        Estacion creada = estacionRepository.save(estacion);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    //  Buscar una estación por ID
    @GetMapping("/{id}")
    public ResponseEntity<Estacion> getById(@PathVariable Long id) {
        Optional<Estacion> estacion = estacionRepository.findById(id);

        return estacion.map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró estación con ID: " + id));
    }

    //  Eliminar estación por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!estacionRepository.existsById(id)) {
            throw new ResourceNotFoundException("No existe estación con ID: " + id + " para eliminar.");
        }

        estacionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
