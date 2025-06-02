package com.data.access.controllers;

import com.data.access.domain.Event;
import com.data.access.domain.Parking;
import com.data.access.services.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.time.Instant;
import java.util.List;
import com.data.access.services.ParkingService;

@RestController
@RequestMapping("/data/events")
public class EventController {

    private final EventService eventService;
    private final ParkingService parkingService;

    public EventController(EventService eventService, ParkingService parkingService) {
        this.eventService = eventService;
        this.parkingService = parkingService;
    }

    public record ParkingSnapshot(Long id, String direction, int bikesAvailable) {
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        return eventService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/parking/{id}")
    public ResponseEntity<List<Event>> getByParking(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.findByParkingId(id));
    }

    @GetMapping("/parking/{id}/range")
    public ResponseEntity<List<Event>> getRange(
            @PathVariable Long id,
            @RequestParam String from,
            @RequestParam String to) {
        return ResponseEntity.ok(eventService.findByParkingIdAndBetween(id, Instant.parse(from), Instant.parse(to)));
    }

    @GetMapping("/timestamp")
    public ResponseEntity<List<Event>> getByTimestamp(@RequestParam String timestamp) {
        return ResponseEntity.ok(eventService.findByTimestamp(Instant.parse(timestamp)));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Event event) {
        try {
            return ResponseEntity.ok(eventService.save(event));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAll() {
        return ResponseEntity.ok(eventService.findAll());
    }

    @GetMapping("/evento/top10-disponibles")
    public ResponseEntity<?> getTop10ByTimestamp(@RequestParam String timestamp) {
        try {
            Instant instant = Instant.parse(timestamp);
            return ResponseEntity.ok(eventService.getTop10ByTimestamp(instant));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Formato inválido de timestamp");
        }
    }

}
