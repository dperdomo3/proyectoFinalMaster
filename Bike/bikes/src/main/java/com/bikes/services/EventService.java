package com.bikes.services;

import com.bikes.domain.Event;
import com.bikes.domain.ParkingSnapshot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    private final RestTemplate restTemplate;
    private final String DATA_SERVICE_URL = "http://localhost:8091/data/events";

    public EventService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Event save(Event event) {
        return restTemplate.postForObject(DATA_SERVICE_URL, event, Event.class);
    }

    public Optional<Event> findById(String id) {
        try {
            ResponseEntity<Event> response = restTemplate.getForEntity(DATA_SERVICE_URL + "/" + id, Event.class);
            return Optional.ofNullable(response.getBody());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<Event> findByParkingId(String parkingId) {
        ResponseEntity<Event[]> response = restTemplate.getForEntity(DATA_SERVICE_URL + "/parking/" + parkingId,
                Event[].class);
        return Arrays.asList(response.getBody());
    }

    public List<Event> findByParkingIdAndBetween(String parkingId, Instant from, Instant to) {
        String url = DATA_SERVICE_URL + "/parking/" + parkingId + "/range?from=" + from + "&to=" + to;
        ResponseEntity<Event[]> response = restTemplate.getForEntity(url, Event[].class);
        return Arrays.asList(response.getBody());
    }

    public List<Event> findByTimestamp(Instant timestamp) {
        String url = DATA_SERVICE_URL + "/timestamp?timestamp=" + timestamp;
        ResponseEntity<Event[]> response = restTemplate.getForEntity(url, Event[].class);
        return Arrays.asList(response.getBody());
    }

    public List<ParkingSnapshot> findTop10ByTimestamp(Instant timestamp) {
        String url = DATA_SERVICE_URL + "/evento/top10-disponibles?timestamp=" + timestamp;
        ResponseEntity<ParkingSnapshot[]> response = restTemplate.getForEntity(url, ParkingSnapshot[].class);
        return Arrays.asList(response.getBody());
    }

}
