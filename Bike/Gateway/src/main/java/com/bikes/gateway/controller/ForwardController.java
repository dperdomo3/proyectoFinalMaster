package com.bikes.gateway.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
public class ForwardController {

    private final RestTemplate restTemplate = new RestTemplate();

    // PÚBLICO
    @GetMapping("/aparcamientos")
    public ResponseEntity<?> forwardAparcamientos(HttpServletRequest request) {
        HttpHeaders headers = buildOptionalHeaders(request);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return restTemplate.exchange("http://localhost:8082/aparcamientos", HttpMethod.GET, entity, String.class);
    }

    // PÚBLICO
    @GetMapping("/aparcamiento/{id}/status")
    public ResponseEntity<?> forwardParkingStatus(@PathVariable String id,
                                                  @RequestParam(required = false) String from,
                                                  @RequestParam(required = false) String to,
                                                  HttpServletRequest request) {
        HttpHeaders headers = buildOptionalHeaders(request);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String fullUrl;
        if (from != null && to != null) {
            fullUrl = "http://localhost:8082/evento/aparcamiento/" + id + "/status?from=" + from + "&to=" + to;
        } else {
            fullUrl = "http://localhost:8082/aparcamiento/" + id + "/status";
        }

        return restTemplate.exchange(fullUrl, HttpMethod.GET, entity, String.class);
    }

    // PÚBLICO
    @GetMapping("/evento/top10-disponibles")
    public ResponseEntity<?> forwardTop10ByTimestamp(@RequestParam String timestamp, HttpServletRequest request) {
        HttpHeaders headers = buildOptionalHeaders(request);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        String fullUrl = "http://localhost:8082/evento/top10-disponibles?timestamp=" + timestamp;

        try {
            return restTemplate.exchange(fullUrl, HttpMethod.GET, entity, String.class);
        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.ok("[]");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno al consultar eventos: " + e.getMessage());
        }
    }

    // PRIVADO - ADMIN
    @PostMapping("/aparcamiento")
    public ResponseEntity<?> createAparcamiento(@RequestBody String body, HttpServletRequest request) {
        String role = (String) request.getAttribute("X-User-Role");
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).body("Acceso denegado: requiere rol ADMIN");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-User-Role", role);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange("http://localhost:8082/aparcamiento", HttpMethod.POST, entity, String.class);
    }

    // PRIVADO - ADMIN
    @PutMapping("/aparcamiento/{id}")
    public ResponseEntity<?> updateAparcamiento(@PathVariable String id,
                                                @RequestBody String body,
                                                HttpServletRequest request) {
        String role = (String) request.getAttribute("X-User-Role");
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).body("Acceso denegado: requiere rol ADMIN");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Role", role);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange("http://localhost:8082/aparcamiento/" + id, HttpMethod.PUT, entity, String.class);
    }

    // PRIVADO - ADMIN
    @DeleteMapping("/aparcamiento/{id}")
    public ResponseEntity<?> deleteAparcamiento(@PathVariable String id, HttpServletRequest request) {
        String role = (String) request.getAttribute("X-User-Role");
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).body("Acceso denegado: requiere rol ADMIN");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Role", role);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return restTemplate.exchange("http://localhost:8082/aparcamiento/" + id, HttpMethod.DELETE, entity, String.class);
    }

    // PRIVADO - APARCAMIENTO
    @PostMapping("/evento/{id}")
    public ResponseEntity<?> forwardEvento(@PathVariable String id,
                                           @RequestBody String body,
                                           HttpServletRequest request) {
        String userId = (String) request.getAttribute("X-User-Id");
        String role = (String) request.getAttribute("X-User-Role");

        if (userId == null || role == null) {
            return ResponseEntity.status(403).body("Faltan headers de autenticación en el gateway");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", userId);
        headers.set("X-User-Role", role);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        String targetUrl = "http://localhost:8082/evento/" + id;

        return restTemplate.exchange(targetUrl, HttpMethod.POST, entity, String.class);
    }

    // PRIVADO - ADMIN
    @GetMapping("/top10")
    public ResponseEntity<?> forwardTop10(HttpServletRequest request) {
        String userId = (String) request.getAttribute("X-User-Id");
        String role = (String) request.getAttribute("X-User-Role");

        if (userId == null || role == null) {
            return ResponseEntity.status(403).body("Faltan headers de autenticación en el gateway");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", userId);
        headers.set("X-User-Role", role);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return restTemplate.exchange("http://localhost:8082/top10", HttpMethod.GET, entity, String.class);
    }

    // ✅ Ayudante para crear headers opcionales en rutas públicas
    private HttpHeaders buildOptionalHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        String userId = (String) request.getAttribute("X-User-Id");
        String role = (String) request.getAttribute("X-User-Role");
        if (userId != null) headers.set("X-User-Id", userId);
        if (role != null) headers.set("X-User-Role", role);
        return headers;
    }
}
