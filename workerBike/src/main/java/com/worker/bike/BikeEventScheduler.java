package com.worker.bike;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class BikeEventScheduler {

    @Value("${bike.gateway.url}")
    private String gatewayUrl;

    @Value("${bike.auth.token}")
    private String token;

    @Value("${bike.evento.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();

    private final List<String> operaciones = Arrays.asList(
            "alquiler", "aparcamiento", "retirada_multiple", "reposicion_multiple"
    );

    @Scheduled(fixedRate = 60000)
    public void enviarEvento() {
        List<AparcamientoDTO> aparcamientos = obtenerAparcamientos();

        if (aparcamientos.isEmpty()) {
            System.err.println("❌ No se encontraron aparcamientos para generar evento.");
            return;
        }

        for (int intento = 0; intento < 10; intento++) {
            AparcamientoDTO aparcamiento = aparcamientos.get(random.nextInt(aparcamientos.size()));
            String operacion = operaciones.get(random.nextInt(operaciones.size()));
            int cantidad = random.nextInt(3) + 1;

            // Validaciones por tipo de operación
            if (operacion.equals("alquiler") && aparcamiento.bikesAvailable < 1) continue;
            if (operacion.equals("aparcamiento") && aparcamiento.freeParkingSpots < 1) continue;
            if (operacion.equals("retirada_multiple") && aparcamiento.bikesAvailable < cantidad) continue;
            if (operacion.equals("reposicion_multiple") && aparcamiento.freeParkingSpots < cantidad) continue;

            // Construcción del payload
            Map<String, Object> payload = new HashMap<>();
            payload.put("operation", operacion);
            payload.put("quantity", cantidad);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + token);
            headers.set("X-User-Role", "APARCAMIENTO");

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

            try {
                String url = gatewayUrl + apiUrl.replace("{id}", String.valueOf(aparcamiento.id));
                ResponseEntity<String> response = restTemplate.exchange(
                        url, HttpMethod.POST, requestEntity, String.class);

                System.out.printf("Evento generado para aparcamiento %d (%s): %s%n",
                        aparcamiento.id, operacion, response.getStatusCode());
                return;

            } catch (Exception e) {
                System.err.printf("Error al enviar evento al aparcamiento %d: %s%n",
                        aparcamiento.id, e.getMessage());
                return;
            }
        }

        System.err.println("No se pudo generar un evento válido después de varios intentos.");
    }

    private List<AparcamientoDTO> obtenerAparcamientos() {
        List<AparcamientoDTO> aparcamientos = new ArrayList<>();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    gatewayUrl + "/aparcamientos", HttpMethod.GET, request, String.class);

            JsonNode json = objectMapper.readTree(response.getBody());

            for (JsonNode node : json) {
                AparcamientoDTO dto = new AparcamientoDTO();
                dto.id = node.get("id").asInt();
                dto.bikesAvailable = node.get("bikesAvailable").asInt();
                dto.freeParkingSpots = node.get("freeParkingSpots").asInt();
                aparcamientos.add(dto);
            }

        } catch (Exception e) {
            System.err.println("Error al obtener aparcamientos: " + e.getMessage());
        }

        return aparcamientos;
    }

    // Clase interna simple para transportar datos del aparcamiento
    static class AparcamientoDTO {
        public int id;
        public int bikesAvailable;
        public int freeParkingSpots;
    }
}
