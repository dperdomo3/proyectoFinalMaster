package com.pollution.gateway.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
public class PollutionProxyController {

    // Cliente HTTP interno para reenviar peticiones
    private final RestTemplate restTemplate = new RestTemplate();

    // Base del servicio objetivo (pollution-service)
    private final String baseUrl = "http://localhost:8083";

    // Construye headers, incluyendo userId y role desde atributos del request
    private HttpHeaders buildHeaders(HttpHeaders incoming, HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAll(incoming.toSingleValueMap());

        // Inserta roles y userId si no están presentes
        if (!headers.containsKey("X-User-Role") && request.getAttribute("X-User-Role") != null) {
            headers.set("X-User-Role", request.getAttribute("X-User-Role").toString());
        }
        if (!headers.containsKey("X-User-Id") && request.getAttribute("X-User-Id") != null) {
            headers.set("X-User-Id", request.getAttribute("X-User-Id").toString());
        }

        return headers;
    }

    // Crea una estación (requiere rol ADMIN)
    @PostMapping("/estacion")
    public ResponseEntity<?> crearEstacion(@RequestBody String body, @RequestHeader HttpHeaders headers,
            HttpServletRequest request) {
        HttpEntity<String> entity = new HttpEntity<>(body, buildHeaders(headers, request));
        return restTemplate.exchange(baseUrl + "/estacion", HttpMethod.POST, entity, String.class);
    }

    // Elimina una estación (requiere rol ADMIN)
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

    // Obtiene todas las estaciones (público)
    @GetMapping("/estaciones")
    public ResponseEntity<?> listarEstaciones(@RequestHeader HttpHeaders headers, HttpServletRequest request) {
        HttpEntity<?> entity = new HttpEntity<>(buildHeaders(headers, request));
        return restTemplate.exchange(baseUrl + "/estaciones", HttpMethod.GET, entity, String.class);
    }

    // Registra una lectura en una estación (requiere rol ESTACION)
    @PostMapping("/estacion/{id}")
    public ResponseEntity<?> registrarLectura(@PathVariable Long id, @RequestBody String body,
            @RequestHeader HttpHeaders headers, HttpServletRequest request) {
        HttpEntity<String> entity = new HttpEntity<>(body, buildHeaders(headers, request));
        return restTemplate.exchange(baseUrl + "/estacion/" + id, HttpMethod.POST, entity, String.class);
    }

    // Obtiene la última lectura de una estación (público)
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
            // Devuelve correctamente 404 en lugar de 500
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    "{\"success\":false,\"message\":\"No se encontró la estación o no tiene lecturas\",\"data\":null}");

        } catch (HttpClientErrorException e) {
            // Otros errores del cliente, como 400, 403, etc.
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());

        } catch (Exception e) {
            // Cualquier otro error inesperado
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"success\":false,\"message\":\"Error interno en el gateway\",\"data\":null}");
        }
    }

    // Obtiene lecturas en un intervalo de tiempo (público)
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
