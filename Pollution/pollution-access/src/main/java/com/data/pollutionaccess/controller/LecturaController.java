package com.data.pollutionaccess.controller;

import com.data.pollutionaccess.model.Lectura;
import com.data.pollutionaccess.repository.LecturaRepository;
import com.data.pollutionaccess.exception.ResourceNotFoundException;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/lectura")
public class LecturaController {

    private final LecturaRepository lecturaRepository;

    public LecturaController(LecturaRepository lecturaRepository) {
        this.lecturaRepository = lecturaRepository;
    }

    //  Crear una nueva lectura. Se establece el timestamp actual.
    @PostMapping
    public ResponseEntity<Lectura> save(@RequestBody Lectura lectura) {
        lectura.setTimestamp(Instant.now());
        Lectura guardada = lecturaRepository.save(lectura);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    //  Obtener todas las lecturas (útil para debug o admins)
    @GetMapping
    public ResponseEntity<List<Lectura>> getAll() {
        return ResponseEntity.ok(lecturaRepository.findAll());
    }

    //  Obtener lecturas por estación entre dos fechas dadas
    @GetMapping(value = "/{estacionId}", params = {"from", "to"})
    public ResponseEntity<List<Lectura>> getByEstacionBetween(
            @PathVariable String estacionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {

        List<Lectura> lecturas = lecturaRepository.findByEstacionIdAndTimestampBetween(estacionId, from, to);

        if (lecturas.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron lecturas entre las fechas para la estación: " + estacionId);
        }

        return ResponseEntity.ok(lecturas);
    }

    //  Obtener la última lectura de una estación específica
    @GetMapping("/{estacionId}")
    public ResponseEntity<Lectura> getUltimaLectura(@PathVariable String estacionId) {
        List<Lectura> lecturas = lecturaRepository.findByEstacionIdOrderByTimestampDesc(estacionId);

        if (lecturas.isEmpty()) {
            throw new ResourceNotFoundException("No hay lecturas registradas para la estación: " + estacionId);
        }

        return ResponseEntity.ok(lecturas.get(0));
    }
}
