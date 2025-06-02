package com.ayuntamiento.gateway.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class ForwardController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String ayuntamientoServiceUrl = "http://localhost:8093/api/ayuntamiento";
    private final String bikeServiceUrl = "http://localhost:8080";
    private final String pollutionServiceUrl = "http://localhost:8085";

    // === AYUNTAMIENTO ===

    @GetMapping("/ayuntamiento/ping")
    public ResponseEntity<String> proxyPing() {
        return restTemplate.getForEntity(ayuntamientoServiceUrl + "/ping", String.class);
    }

    @GetMapping("/ayuntamiento/aggregatedData")
    public ResponseEntity<Object> proxyAggregatedData() {
        return restTemplate.getForEntity(ayuntamientoServiceUrl + "/aggregatedData", Object.class);
    }

    @GetMapping("/ayuntamiento/aggregateData")
    public ResponseEntity<String> proxyAggregateData() {
        return restTemplate.getForEntity(ayuntamientoServiceUrl + "/aggregateData", String.class);
    }

    @GetMapping("/ayuntamiento/aparcamientoCercano")
    public ResponseEntity<Object> proxyAparcamientoCercano(
            @RequestParam double lat,
            @RequestParam double lon) {
        String url = ayuntamientoServiceUrl + "/aparcamientoCercano?lat=" + lat + "&lon=" + lon;
        return restTemplate.getForEntity(url, Object.class);
    }

    // === BICICLETA ===
    @GetMapping("/aparcamientos")
    public ResponseEntity<Object> proxyBikeAparcamientos() {
        return restTemplate.getForEntity(bikeServiceUrl + "/aparcamientos", Object.class);
    }

    @GetMapping("/aparcamiento/{id}/status")
    public ResponseEntity<Object> proxyBikeStatus(
            @PathVariable Long id,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        String url;

        if (from != null && to != null) {
            // Este es el endpoint del microservicio de eventos
            url = bikeServiceUrl + "/aparcamiento/" + id + "/status?from=" + from + "&to=" + to;
        } else {
            // Este es el endpoint de estado actual
            url = bikeServiceUrl + "/aparcamiento/" + id + "/status";
        }

        return restTemplate.getForEntity(url, Object.class);
    }

    @PostMapping("/aparcamiento")
    public ResponseEntity<Object> proxyCrearAparcamiento(HttpServletRequest request, @RequestBody String body) {
        if (!hasAdminRole(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado: se requiere rol ADMIN");
        }
        return forwardPost(request, bikeServiceUrl + "/aparcamiento", body);
    }

    @DeleteMapping("/aparcamiento/{id}")
    public ResponseEntity<Object> proxyEliminarAparcamiento(HttpServletRequest request, @PathVariable Long id) {
        if (!hasAdminRole(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado: se requiere rol ADMIN");
        }
        return forwardDelete(request, bikeServiceUrl + "/aparcamiento/" + id);
    }

    // === POLUCIÓN ===

    @GetMapping("/estaciones")
    public ResponseEntity<Object> proxyPollutionEstaciones() {
        return restTemplate.getForEntity(pollutionServiceUrl + "/estaciones", Object.class);
    }

    @GetMapping("/estacion/{id}/status")
    public ResponseEntity<Object> proxyPollutionStatus(
            @PathVariable Long id,
            @RequestParam String from,
            @RequestParam String to) {
        String url = pollutionServiceUrl + "/estacion/" + id + "/status?from=" + from + "&to=" + to;
        return restTemplate.getForEntity(url, Object.class);
    }

    @PostMapping("/estacion")
    public ResponseEntity<Object> proxyCrearEstacion(HttpServletRequest request, @RequestBody String body) {
        if (!hasAdminRole(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado: se requiere rol ADMIN");
        }
        return forwardPost(request, pollutionServiceUrl + "/estacion", body);
    }

    @DeleteMapping("/estacion/{id}")
    public ResponseEntity<Object> proxyEliminarEstacion(HttpServletRequest request, @PathVariable Long id) {
        if (!hasAdminRole(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado: se requiere rol ADMIN");
        }
        return forwardDelete(request, pollutionServiceUrl + "/estacion/" + id);
    }

    // === MÉTODOS DE APOYO ===
    private boolean hasAdminRole(HttpServletRequest request) {
        String role = (String) request.getAttribute("X-User-Role");
        System.out.println("Rol detectado en gateway: " + role);
        return role != null && role.equalsIgnoreCase("ADMIN");
    }

    private ResponseEntity<Object> forwardPost(HttpServletRequest request, String url, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", request.getHeader("Authorization"));
        headers.set("X-User-Role", (String) request.getAttribute("X-User-Role")); //Añadir rol
        headers.set("X-User-Id", (String) request.getAttribute("X-User-Id")); //Añadir userId
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
    }

    private ResponseEntity<Object> forwardDelete(HttpServletRequest request, String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", request.getHeader("Authorization"));
        headers.set("X-User-Role", (String) request.getAttribute("X-User-Role")); //Añadir rol
        headers.set("X-User-Id", (String) request.getAttribute("X-User-Id")); // Añadir userId
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.DELETE, entity, Object.class);
    }

}
