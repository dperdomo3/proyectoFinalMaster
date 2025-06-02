package com.bikes.controller;

import com.bikes.domain.Parking;
import com.bikes.services.ParkingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class ParkingController {

    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    // ✅ GET /aparcamientos (público)
    @GetMapping("/aparcamientos")
    public ResponseEntity<List<Parking>> getAllParkings() {
        List<Parking> parkings = parkingService.findAll();
        return ResponseEntity.ok(parkings); // 200 OK
    }

    // ✅ GET /aparcamiento/{id}/status (público)
    @GetMapping("/aparcamiento/{id}/status")
    public ResponseEntity<?> getParkingStatus(@PathVariable Long id) {
        return parkingService.findById(id)
                .<ResponseEntity<?>>map(parking -> ResponseEntity.ok(parking))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Aparcamiento no encontrado"));
    }

    // POST /aparcamiento (crear)
    @PostMapping("/aparcamiento")
    public ResponseEntity<?> createParking(@RequestBody Parking parking,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).body("Acceso denegado: requiere rol ADMIN");
        }

        if (parkingService.existsById(parking.getId())) {
            return ResponseEntity.status(400).body("Ya existe un aparcamiento con ese ID");
        }

        Parking saved = parkingService.save(parking);
        return ResponseEntity.status(201).body(saved); // 201 Created
    }

    // PUT /aparcamiento/{id} (editar)
    @PutMapping("/aparcamiento/{id}")
    public ResponseEntity<?> updateParking(@PathVariable Long id,
            @RequestBody Parking updated,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).body("Acceso denegado: requiere rol ADMIN");
        }

        try {
            Parking result = parkingService.updateParking(id, updated);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // DELETE /aparcamiento/{id}
    @DeleteMapping("/aparcamiento/{id}")
    public ResponseEntity<?> deleteParking(@PathVariable Long id,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).body("Acceso denegado: requiere rol ADMIN");
        }

        if (!parkingService.existsById(id)) {
            return ResponseEntity.status(404).body("Aparcamiento no encontrado");
        }

        parkingService.deleteById(id);
        return ResponseEntity.status(204).build(); // 204 No Content
    }

    @GetMapping("/top10")
    public ResponseEntity<List<Parking>> getTop10() {
        return ResponseEntity.ok(parkingService.getTop10());
    }

}
