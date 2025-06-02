package com.ayuntamiento.service.controller;

import com.ayuntamiento.service.dto.AggregatedDataDTO;
import com.ayuntamiento.service.service.AccesoRemotoService;
import com.ayuntamiento.service.service.IntegracionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ayuntamiento")
public class IntegracionController {

    @Autowired
    private IntegracionService integracionService;

    @Autowired
    private AccesoRemotoService accesoRemotoService;

    @GetMapping("/only-estaciones")
    public ResponseEntity<Object> obtenerSoloEstaciones() {
        return integracionService.obtenerSoloEstaciones();
    }

    @GetMapping("/only-aparcamientos")
    public ResponseEntity<Object> obtenerSoloAparcamientos() {
        return integracionService.obtenerSoloAparcamientos();
    }

    @GetMapping("/aparcamientoCercano")
    public ResponseEntity<Object> aparcamientoCercano(@RequestParam double lat, @RequestParam double lon) {
        Object aparcamientoMasCercano = integracionService.obtenerAparcamientoCercano(lat, lon);

        if (aparcamientoMasCercano == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay aparcamientos con bicis disponibles.");
        }
        return ResponseEntity.ok(aparcamientoMasCercano);
    }

    @GetMapping("/aggregatedData")
    public ResponseEntity<AggregatedDataDTO> obtenerUltimoAgregado() {
        AggregatedDataDTO dto = accesoRemotoService.obtenerUltimo();

        if (dto == null || dto.getAggregatedData() == null || dto.getAggregatedData().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/aggregateData")
    public ResponseEntity<Object> generarYGuardarDatos() {
        AggregatedDataDTO dto = integracionService.obtenerDatosAgregados();

        if (dto == null || dto.getAggregatedData() == null || dto.getAggregatedData().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No hay datos para agregar");
        }

        accesoRemotoService.guardarDatos(dto.getAggregatedData());

        return ResponseEntity.ok("✅ Datos agregados guardados correctamente en ayuntamiento-access");
    }

    @PostMapping("/estacion")
    public ResponseEntity<Object> crearEstacion(@RequestHeader("Authorization") String token,
            @RequestBody String body) {
        return integracionService.crearEstacion(token, body);
    }

    @DeleteMapping("/estacion/{id}")
    public ResponseEntity<Object> eliminarEstacion(@RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        return integracionService.eliminarEstacion(token, id);
    }

    @PostMapping("/aparcamiento")
    public ResponseEntity<Object> crearAparcamiento(@RequestHeader("Authorization") String token,
            @RequestBody String body) {
        return integracionService.crearAparcamiento(token, body);
    }

    @DeleteMapping("/aparcamiento/{id}")
    public ResponseEntity<Object> eliminarAparcamiento(@RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        return integracionService.eliminarAparcamiento(token, id);
    }
}
