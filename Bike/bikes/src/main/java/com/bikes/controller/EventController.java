package com.bikes.controller;

import com.bikes.domain.Event;
import com.bikes.domain.ParkingSnapshot;
import com.bikes.services.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.time.Instant;

@RestController
@RequestMapping("/evento")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // Permite a un aparcamiento registrar un evento (aparcamiento,
    // alquiler,reposición, retirada múltiple)
    // Formato esperado:
    // { "operation": "alquiler", "bikesAvailable": 12, "freeParkingSpots": 8}
    @PostMapping("/{id}")
    public ResponseEntity<?> createEvent(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @RequestBody Event event) {
        if (!"APARCAMIENTO".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).body("Acceso denegado: requiere rol APARCAMIENTO");
        }

        // Asociamos el ID del path a la entidad
        event.setParkingId(id);

        // Si no se pasó un timestamp, lo ponemos ahora
        if (event.getTimestamp() == null) {
            event.setTimestamp(Instant.now());
        }

        Event saved = eventService.save(event);
        return ResponseEntity.status(201).body(saved); // 201 Created
    }

    // Devuelve todos los eventos registrados para un aparcamiento en un intervalo
    // de tiempo.
    // GET
    // /evento/aparcamiento/{id}/status?from=2024-01-01T00:00:00Z&to=2024-01-02T00:00:00Z
    @GetMapping("/aparcamiento/{id}/status")
    public ResponseEntity<?> getStatusHistory(
            @PathVariable String id,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        try {
            if (from == null || to == null) {
                return ResponseEntity.status(400).body("Parámetros 'from' y 'to' son obligatorios (formato ISO 8601)");
            }

            Instant fromInstant = Instant.parse(from);
            Instant toInstant = Instant.parse(to);

            List<Event> events = eventService.findByParkingIdAndBetween(id, fromInstant, toInstant);

            if (events.isEmpty()) {
                return ResponseEntity.status(404).body("No se encontraron eventos en ese rango");
            }

            return ResponseEntity.ok(events); // 200 OK
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(400)
                    .body("Formato de fecha inválido. Usa formato ISO 8601 (e.g. 2024-01-01T00:00:00Z)");
        }
    }

    // Devuelve los 10 aparcamientos con más bicicletas disponibles en un instante
    // GET /evento/top10-disponibles?timestamp=2024-01-01T00:00:00Z
    @GetMapping("/top10-disponibles")
    public ResponseEntity<?> getTop10ByTimestamp(@RequestParam String timestamp) {
        try {
            Instant instant = Instant.parse(timestamp);
            List<ParkingSnapshot> top10 = eventService.findTop10ByTimestamp(instant);

            if (top10.isEmpty()) {
                return ResponseEntity.status(404).body("No se encontraron datos");
            }

            return ResponseEntity.ok(top10);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Formato inválido de timestamp");
        }
    }

}
