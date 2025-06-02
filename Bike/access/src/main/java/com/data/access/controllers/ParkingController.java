package com.data.access.controllers;

import com.data.access.domain.Parking;
import com.data.access.services.ParkingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/data/parkings")
public class ParkingController {

    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @GetMapping
    public ResponseEntity<List<Parking>> getAll() {
        return ResponseEntity.ok(parkingService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return parkingService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Parking> create(@RequestBody Parking parking) {
        return ResponseEntity.ok(parkingService.save(parking));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (parkingService.existsById(id)) {
            parkingService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/top10")
    public ResponseEntity<List<Parking>> top10() {
        return ResponseEntity.ok(parkingService.findTop10ByBikesAvailable());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Parking updated) {
        return parkingService.findById(id)
                .map(existing -> {
                    if (updated.getDirection() != null) {
                        existing.setDirection(updated.getDirection());
                    }
                    if (updated.getLatitude() != 0.0f) {
                        existing.setLatitude(updated.getLatitude());
                    }
                    if (updated.getLongitude() != 0.0f) {
                        existing.setLongitude(updated.getLongitude());
                    }
                    if (updated.getBikesCapacity() != 0) {
                        existing.setBikesCapacity(updated.getBikesCapacity());
                    }
                    if (updated.getBikesAvailable() != 0) {
                        existing.setBikesAvailable(updated.getBikesAvailable());
                    }
                    if (updated.getFreeParkingSpots() != 0) {
                        existing.setFreeParkingSpots(updated.getFreeParkingSpots());
                    }

                    return ResponseEntity.ok(parkingService.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
