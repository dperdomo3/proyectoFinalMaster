package com.data.access.services;

import com.data.access.domain.Event;
import com.data.access.domain.Parking;
import com.data.access.repositories.EventRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import com.data.access.services.ParkingService;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ParkingService parkingService;

    public record ParkingSnapshot(Long id, String direction, int bikesAvailable) {
    }

    public Event save(Event event) {
        Long parkingId = event.getParkingId();
        Parking parking = parkingService.findById(parkingId)
                .orElseThrow(() -> new IllegalArgumentException("Aparcamiento no encontrado"));

        String operation = event.getOperation().toLowerCase();
        int quantity;

        switch (operation) {
            case "aparcamiento", "alquiler" -> quantity = 1;
            case "reposicion_multiple", "retirada_multiple" -> {
                if (event.getQuantity() <= 0) {
                    throw new IllegalArgumentException("Debes especificar un valor válido para 'quantity'");
                }
                quantity = event.getQuantity();
            }
            default -> throw new IllegalArgumentException("Operación desconocida: " + event.getOperation());
        }

        event.setQuantity(quantity);

        switch (event.getOperation().toLowerCase()) {
            case "aparcamiento" -> {
                if (parking.getFreeParkingSpots() <= 0)
                    throw new IllegalArgumentException("No hay espacios disponibles para aparcar");
                parking.setBikesAvailable(parking.getBikesAvailable() + 1);
                parking.setFreeParkingSpots(parking.getFreeParkingSpots() - 1);
            }
            case "reposicion_multiple" -> {
                if (parking.getFreeParkingSpots() < quantity)
                    throw new IllegalArgumentException(
                            "No hay espacios suficientes para reponer " + quantity + " bicis");
                parking.setBikesAvailable(parking.getBikesAvailable() + quantity);
                parking.setFreeParkingSpots(parking.getFreeParkingSpots() - quantity);
            }
            case "alquiler" -> {
                if (parking.getBikesAvailable() <= 0)
                    throw new IllegalArgumentException("No hay bicicletas disponibles para alquilar");
                parking.setBikesAvailable(parking.getBikesAvailable() - 1);
                parking.setFreeParkingSpots(parking.getFreeParkingSpots() + 1);
            }
            case "retirada_multiple" -> {
                if (parking.getBikesAvailable() < quantity)
                    throw new IllegalArgumentException("No hay suficientes bicis para retirar " + quantity);
                parking.setBikesAvailable(parking.getBikesAvailable() - quantity);
                parking.setFreeParkingSpots(parking.getFreeParkingSpots() + quantity);
            }
            default -> throw new IllegalArgumentException("Operación desconocida: " + event.getOperation());
        }

        // Aquí guardamos el estado actualizado en el evento
        event.setBikesAvailable(parking.getBikesAvailable());
        event.setFreeParkingSpots(parking.getFreeParkingSpots());

        parkingService.save(parking);
        return eventRepository.save(event);
    }

    public Optional<Event> findById(String id) {
        return eventRepository.findById(id);
    }

    public List<Event> findByParkingId(Long parkingId) {
        return eventRepository.findByParkingId(parkingId);
    }

    public List<Event> findByParkingIdAndBetween(Long parkingId, Instant from, Instant to) {
        return eventRepository.findByParkingIdAndTimestampBetween(parkingId, from, to);
    }

    public List<Event> findByTimestamp(Instant timestamp) {
        return eventRepository.findByTimestamp(timestamp);
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public Optional<Event> findLastEventBefore(Long parkingId, Instant timestamp) {
        return eventRepository.findTopByParkingIdAndTimestampLessThanEqualOrderByTimestampDesc(parkingId, timestamp);
    }

    // Obtiene los 10 aparcamientos con más bicicletas disponibles en un instante dado
    public List<ParkingSnapshot> getTop10ByTimestamp(Instant timestamp) {
    List<Parking> allParkings = parkingService.findAll().stream()
            .filter(p -> p.getCreatedAt() != null && !p.getCreatedAt().isAfter(timestamp)) // creado antes del timestamp
            .toList();

    return allParkings.stream()
            .map(parking -> {
                Optional<Event> lastEvent = findLastEventBefore(parking.getId(), timestamp);

                // Solo incluimos parkings que tengan al menos un evento registrado en o antes del timestamp
                return lastEvent.map(event -> new ParkingSnapshot(
                        parking.getId(),
                        parking.getDirection(),
                        event.getBikesAvailable()
                ));
            })
            .filter(Optional::isPresent)
            .map(Optional::get)
            .sorted((a, b) -> Integer.compare(b.bikesAvailable(), a.bikesAvailable()))
            .limit(10)
            .toList();
}

}
