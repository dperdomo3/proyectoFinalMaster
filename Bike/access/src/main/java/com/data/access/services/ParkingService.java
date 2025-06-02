package com.data.access.services;

import com.data.access.domain.Parking;
import com.data.access.repositories.ParkingRepository;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ParkingService {

    private final ParkingRepository parkingRepository;

    public ParkingService(ParkingRepository parkingRepository) {
        this.parkingRepository = parkingRepository;
    }

    public List<Parking> findAll() {
        return parkingRepository.findAll();
    }

    public Optional<Parking> findById(Long id) {
        return parkingRepository.findById(id);
    }

    public Parking save(Parking parking) {
        if (parking.getId() == null) { // nuevo aparcamiento
            parking.setCreatedAt(Instant.now());
            parking.setBikesAvailable(parking.getBikesCapacity());
        }
        return parkingRepository.save(parking);
    }

    public void deleteById(Long id) {
        parkingRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return parkingRepository.existsById(id);
    }

    public void updateAvailability(Long parkingId, int bikesAvailable, int freeParkingSpots) {
        parkingRepository.findById(parkingId).ifPresent(parking -> {
            parking.setBikesAvailable(bikesAvailable);
            parking.setFreeParkingSpots(freeParkingSpots);
            parkingRepository.save(parking);
        });
    }

    public List<Parking> findTop10ByBikesAvailable() {
        return parkingRepository.findAll(Sort.by(Sort.Direction.DESC, "bikesAvailable"))
                .stream().limit(10).toList();
    }

}
