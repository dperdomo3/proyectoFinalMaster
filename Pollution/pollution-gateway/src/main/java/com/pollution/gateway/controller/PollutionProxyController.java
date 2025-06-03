package com.pollution.gateway.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
public class PollutionProxyController {

    private final RestTemplate restTemplate;

    // nombre de servicio registrado en Eureka
    private final String baseUrl = "http://pollution-service";

    public PollutionProxyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private HttpHeaders buildHeaders(HttpHeaders incoming, HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAll(incoming.toSingleValueMap());

        if (!headers.containsKey("X-User-Role") && request.getAttribute("X-User-Role") != null) {
            headers.set("X-User-Role", request.getAttribute("X-User-Role").toString());
        }
        if (!headers.containsKey("X-User-Id") && request.getAttribute("X-User-Id") != null) {
            headers.set("X-User-Id", request.getAttribute("X-User-Id").toString());
        }

        return headers;
    }

    @PostMapping("/estacion")
    public ResponseEntity<?> crearEstacion(@RequestBody String body, @RequestHeader HttpHeaders headers,
                                           HttpServletRequest request) {
        HttpEntity<String> entity = new HttpEntity<>(body, buildHeaders(headers, request));
        return restTemplate.exchange(baseUrl + "/estacion", HttpMethod.POST, entity, String.class);
    }

    @DeleteMapping("/estacion/{id}")
    public ResponseEntity<?> eliminarEstacion(@PathVariable Long id,
                                              @RequestHeader HttpHeaders headers,
                                              HttpServletRequest request) {
        try {
            HttpEntity<?> entity = new HttpEntity<>(buildHeaders(headers, request));
            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl + "/estacion/" + id,
                    HttpMethod.DELETE,
                    entity,
                    String.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());

        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"success\":false,\"message\":\"No se encontró la estación para eliminar\",\"data\":null}");
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"success\":false,\"message\":\"Error interno al eliminar estación\",\"data\":null}");
        }
    }

    @GetMapping("/estaciones")
    public ResponseEntity<?> listarEstaciones(@RequestHeader HttpHeaders headers, HttpServletRequest request) {
        HttpEntity<?> entity = new HttpEntity<>(buildHeaders(headers, request));
        return restTemplate.exchange(baseUrl + "/estaciones", HttpMethod.GET, entity, String.class);
    }

    @PostMapping("/estacion/{id}")
    public ResponseEntity<?> registrarLectura(@PathVariable Long id, @RequestBody String body,
                                              @RequestHeader HttpHeaders headers, HttpServletRequest request) {
        HttpEntity<String> entity = new HttpEntity<>(body, buildHeaders(headers, request));
        return restTemplate.exchange(baseUrl + "/estacion/" + id, HttpMethod.POST, entity, String.class);
    }

    @GetMapping("/estacion/{id}/status")
    public ResponseEntity<?> obtenerUltimaLectura(@PathVariable Long id, @RequestHeader HttpHeaders headers,
                                                  HttpServletRequest request) {
        try {
            HttpEntity<?> entity = new HttpEntity<>(buildHeaders(headers, request));
            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl + "/estacion/" + id + "/status",
                    HttpMethod.GET,
                    entity,
                    String.class);
            return ResponseEntity.ok(response.getBody());

        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    "{\"success\":false,\"message\":\"No se encontró la estación o no tiene lecturas\",\"data\":null}");
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"success\":false,\"message\":\"Error interno en el gateway\",\"data\":null}");
        }
    }

    @GetMapping(value = "/estacion/{id}/status", params = { "from", "to" })
    public ResponseEntity<?> lecturasPorIntervalo(
            @PathVariable Long id,
            @RequestParam String from,
            @RequestParam String to,
            @RequestHeader HttpHeaders headers,
            HttpServletRequest request) {
        HttpEntity<?> entity = new HttpEntity<>(buildHeaders(headers, request));
        String url = baseUrl + "/estacion/" + id + "/status?from=" + from + "&to=" + to;
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }
}
