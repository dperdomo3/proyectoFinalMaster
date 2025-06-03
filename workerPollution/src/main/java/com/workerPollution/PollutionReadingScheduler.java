package com.workerPollution;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class PollutionReadingScheduler {

    @Value("${pollution.gateway.url}")
    private String gatewayUrl;

    @Value("${pollution.auth.token}")
    private String token;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();

    public PollutionReadingScheduler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedRate = 60000)
    public void enviarLectura() {
        System.out.println("Ejecutando tarea de envío de lectura...");
        List<Long> estaciones = obtenerIdsDeEstaciones();

        if (estaciones.isEmpty()) {
            System.err.println("No se encontraron estaciones para generar lectura.");
            return;
        }

        Long estacionId = estaciones.get(random.nextInt(estaciones.size()));

        Map<String, Object> body = new HashMap<>();
        body.put("estacionId", estacionId);
        body.put("nitricOxides", random.nextDouble(100));
        body.put("nitrogenDioxides", random.nextDouble(100));
        body.put("VOCs_NMHC", random.nextDouble(20));
        body.put("PM2_5", random.nextDouble(50));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        headers.set("X-User-Role", "ESTACION");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            String url = gatewayUrl + "/estacion/" + estacionId;
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            System.out.printf("Lectura registrada en estación %d: %s\n", estacionId, response.getStatusCode());
        } catch (Exception e) {
            System.err.println("Error registrando lectura: " + e.getMessage());
        }
    }

    private List<Long> obtenerIdsDeEstaciones() {
        List<Long> ids = new ArrayList<>();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    gatewayUrl + "/estaciones",
                    HttpMethod.GET,
                    request,
                    String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.get("data");

            if (data != null && data.isArray()) {
                for (JsonNode node : data) {
                    ids.add(node.get("id").asLong());
                }
            }

        } catch (Exception e) {
            System.err.println("Error al obtener estaciones: " + e.getMessage());
        }

        return ids;
    }
}
