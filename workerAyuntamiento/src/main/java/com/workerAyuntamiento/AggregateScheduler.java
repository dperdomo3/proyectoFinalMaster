package com.workerAyuntamiento;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AggregateScheduler {

    @Value("${ayuntamiento.gateway.url}")
    private String url;

    @Value("${ayuntamiento.auth.token}")
    private String token;

    private final RestTemplate restTemplate;

    public AggregateScheduler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Scheduled(initialDelay = 60000, fixedRateString = "${agg.interval:120000}") // cada 2 minutos por defecto
    public void ejecutarTarea() {
        System.out.println("Ejecutando petición de agregación al gateway...");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("X-User-Role", "SERVICIO");

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url + "/api/ayuntamiento/aggregateData",
                    HttpMethod.GET,
                    request,
                    String.class
            );

            System.out.println("Respuesta del gateway: " + response.getStatusCode());
            System.out.println(response.getBody());
        } catch (Exception e) {
            System.err.println("Error al generar datos agregados: " + e.getMessage());
        }
    }
}
