package com.bikes.services;

import com.bikes.domain.Parking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ParkingService {

    private final RestTemplate restTemplate;
    private final String DATA_SERVICE_URL = "http://localhost:8091/data/parkings";

    public ParkingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Parking> findAll() {
        ResponseEntity<Parking[]> response = restTemplate.getForEntity(DATA_SERVICE_URL, Parking[].class);
        return Arrays.asList(response.getBody());
    }

    public Optional<Parking> findById(Long id) {
        try {
            ResponseEntity<Parking> response = restTemplate.getForEntity(DATA_SERVICE_URL + "/" + id, Parking.class);
            return Optional.ofNullable(response.getBody());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Parking save(Parking parking) {
        HttpEntity<Parking> request = new HttpEntity<>(parking);
        return restTemplate.postForObject(DATA_SERVICE_URL, request, Parking.class);
    }

    public void deleteById(Long id) {
        restTemplate.delete(DATA_SERVICE_URL + "/" + id);
    }

    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    public List<Parking> getTop10() {
        ResponseEntity<Parking[]> response = restTemplate.getForEntity(
                "http://localhost:8091/data/parkings/top10", Parking[].class);
        return Arrays.asList(response.getBody());
    }

    public Parking updateParking(Long id, Parking updated) {
        Optional<Parking> optional = findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("Aparcamiento no encontrado");
        }

        Parking existing = optional.get();

        // Solo actualizamos si vienen nuevos valores
        if (updated.getDirection() != null) {
            existing.setDirection(updated.getDirection());
        }

        if (updated.getLatitude() != 0.0f) {
            existing.setLatitude(updated.getLatitude());
        }

        if (updated.getLongitude() != 0.0f) {
            existing.setLongitude(updated.getLongitude());
        }

        // Actualizar capacidad y ajustar disponibilidad
        int oldCapacity = existing.getBikesCapacity();
        int newCapacity = updated.getBikesCapacity();

        if (newCapacity != 0 && newCapacity != oldCapacity) {
            int diff = newCapacity - oldCapacity;
            int newAvailable = existing.getBikesAvailable() + diff;

            if (newAvailable < 0) {
                throw new IllegalArgumentException("No se puede reducir la capacidad: quedarían bicis en negativo.");
            }

            existing.setBikesCapacity(newCapacity);
            existing.setBikesAvailable(newAvailable);
        }

        // Enviar PUT al microservicio de datos
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Parking> request = new HttpEntity<>(existing, headers);
        ResponseEntity<Parking> response = restTemplate.exchange(
                DATA_SERVICE_URL + "/" + id, HttpMethod.PUT, request, Parking.class);

        return response.getBody();
    }
}
