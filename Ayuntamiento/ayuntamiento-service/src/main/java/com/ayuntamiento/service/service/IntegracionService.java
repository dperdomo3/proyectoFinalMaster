package com.ayuntamiento.service.service;

import com.ayuntamiento.service.dto.AggregatedDataDTO;
import com.ayuntamiento.service.dto.AirQualityDTO;
import com.ayuntamiento.service.dto.EstacionAparcamientoData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class IntegracionService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String GATEWAY_BASE_URL = "http://ayuntamiento-gateway/api";

    public ResponseEntity<Object> obtenerSoloEstaciones() {
        try {
            ResponseEntity<Object> response = restTemplate.getForEntity(
                    GATEWAY_BASE_URL + "/estaciones", Object.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener estaciones: " + e.getMessage());
        }
    }

    public ResponseEntity<Object> obtenerSoloAparcamientos() {
        try {
            ResponseEntity<Object> response = restTemplate.getForEntity(
                    GATEWAY_BASE_URL + "/aparcamientos", Object.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener aparcamientos: " + e.getMessage());
        }
    }

    public Object obtenerAparcamientoCercano(double lat, double lon) {
        try {
            ResponseEntity<List> response = restTemplate.getForEntity(
                    GATEWAY_BASE_URL + "/aparcamientos", List.class);
            List<Map<String, Object>> aparcamientos = response.getBody();
            return buscarAparcamientoCercano(aparcamientos, lat, lon);
        } catch (Exception e) {
            System.out.println("Error obteniendo aparcamientos: " + e.getMessage());
            return null;
        }
    }

    public AggregatedDataDTO obtenerDatosAgregados() {
        List<EstacionAparcamientoData> resultados = new ArrayList<>();

        try {
            ResponseEntity<List<Map<String, Object>>> aparcamientosResponse = restTemplate.exchange(
                    GATEWAY_BASE_URL + "/aparcamientos",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    });
            List<Map<String, Object>> aparcamientos = aparcamientosResponse.getBody();
            System.out.println("🔍 Aparcamientos encontrados: " + aparcamientos.size());

            ResponseEntity<Map<String, Object>> estacionesResponse = restTemplate.exchange(
                    GATEWAY_BASE_URL + "/estaciones",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    });
            Map<String, Object> estacionesWrapper = estacionesResponse.getBody();
            List<Map<String, Object>> estaciones = (List<Map<String, Object>>) estacionesWrapper.get("data");
            System.out.println("🔍 Estaciones encontradas: " + estaciones.size());

            for (Map<String, Object> aparcamiento : aparcamientos) {
                Long id = Long.parseLong(aparcamiento.get("id").toString());
                double lat = Double.parseDouble(aparcamiento.get("latitude").toString());
                double lon = Double.parseDouble(aparcamiento.get("longitude").toString());
                System.out.println("\n🚲 Procesando aparcamiento ID " + id);

                try {
                    Instant to = Instant.now();
                    Instant from = to.minus(Duration.ofHours(24));

                    UriComponentsBuilder uriBuilder = UriComponentsBuilder
                            .fromHttpUrl(GATEWAY_BASE_URL + "/aparcamiento/" + id + "/status")
                            .queryParam("from", from.toString())
                            .queryParam("to", to.toString());

                    ResponseEntity<List<Map<String, Object>>> bikeEventsResponse = restTemplate.exchange(
                            uriBuilder.toUriString(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                            });
                    List<Map<String, Object>> bikeEvents = bikeEventsResponse.getBody();

                    if (bikeEvents == null || bikeEvents.isEmpty()) {
                        System.out.println("No hay eventos de bicicletas para aparcamiento ID " + id);
                        continue;
                    }

                    double avgBikes = bikeEvents.stream()
                            .mapToDouble(e -> Double.parseDouble(e.get("bikesAvailable").toString()))
                            .average()
                            .orElse(0.0);
                    System.out.println("Promedio bikesAvailable: " + avgBikes);

                    Map<String, Object> estacionMasCercana = null;
                    double menorDistancia = Double.MAX_VALUE;
                    for (Map<String, Object> estacion : estaciones) {
                        double latE = Double.parseDouble(estacion.get("latitud").toString());
                        double lonE = Double.parseDouble(estacion.get("longitud").toString());
                        double distancia = calcularDistancia(lat, lon, latE, lonE);
                        if (distancia < menorDistancia) {
                            menorDistancia = distancia;
                            estacionMasCercana = estacion;
                        }
                    }

                    if (estacionMasCercana == null) {
                        System.out.println("No se encontró estación cercana para aparcamiento ID " + id);
                        continue;
                    }

                    Long estacionId = Long.parseLong(estacionMasCercana.get("id").toString());
                    System.out.println(
                            "Estación más cercana: ID " + estacionId + " (distancia: " + menorDistancia + " km)");

                    String urlPollution = String.format(
                            GATEWAY_BASE_URL + "/estacion/%d/status?from=%s&to=%s",
                            estacionId, from.toString(), to.toString());

                    ResponseEntity<Map<String, Object>> pollutionResponse = restTemplate.exchange(
                            urlPollution, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                            });
                    Map<String, Object> pollutionWrapper = pollutionResponse.getBody();
                    List<Map<String, Object>> pollutionLectures = (List<Map<String, Object>>) pollutionWrapper
                            .get("data");

                    if (pollutionLectures == null || pollutionLectures.isEmpty()) {
                        System.out.println("No hay lecturas de polución para estación ID " + estacionId);
                        continue;
                    }

                    System.out.println("Lecturas encontradas: " + pollutionLectures.size());

                    double totalNO = 0, totalNO2 = 0, totalVOCs = 0, totalPM = 0;
                    int count = pollutionLectures.size();

                    for (Map<String, Object> lectura : pollutionLectures) {
                        totalNO += Double.parseDouble(lectura.get("nitricOxides").toString());
                        totalNO2 += Double.parseDouble(lectura.get("nitrogenDioxides").toString());
                        totalVOCs += Double.parseDouble(lectura.get("VOCs_NMHC").toString());
                        totalPM += Double.parseDouble(lectura.get("PM2_5").toString());
                    }

                    AirQualityDTO airQuality = new AirQualityDTO();
                    airQuality.setNitricOxides(totalNO / count);
                    airQuality.setNitrogenDioxides(totalNO2 / count);
                    airQuality.setVOCs_NMHC(totalVOCs / count);
                    airQuality.setPM2_5(totalPM / count);

                    EstacionAparcamientoData dato = new EstacionAparcamientoData();
                    dato.setId(id);
                    dato.setAverage_bikesAvailable(avgBikes);
                    dato.setAir_quality(airQuality);

                    resultados.add(dato);
                    System.out.println("Dato agregado al resultado");
                } catch (Exception e) {
                    System.out.println("Error con aparcamiento ID " + id + ": " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.out.println("Error general en la agregación: " + e.getMessage());
        }

        AggregatedDataDTO resultadoFinal = new AggregatedDataDTO();
        resultadoFinal.setTimeStamp(Instant.now());
        resultadoFinal.setAggregatedData(resultados);

        System.out.println("Total de datos agregados: " + resultados.size());
        return resultadoFinal;
    }

    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public ResponseEntity<Object> crearEstacion(String token, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(GATEWAY_BASE_URL + "/estacion", HttpMethod.POST, entity, Object.class);
    }

    public ResponseEntity<Object> eliminarEstacion(String token, Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(GATEWAY_BASE_URL + "/estacion/" + id, HttpMethod.DELETE, entity, Object.class);
    }

    public ResponseEntity<Object> crearAparcamiento(String token, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(GATEWAY_BASE_URL + "/aparcamiento", HttpMethod.POST, entity, Object.class);
    }

    public ResponseEntity<Object> eliminarAparcamiento(String token, Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(GATEWAY_BASE_URL + "/aparcamiento/" + id, HttpMethod.DELETE, entity,
                Object.class);
    }

    public Map<String, Object> buscarAparcamientoCercano(List<Map<String, Object>> aparcamientos, double lat,
            double lon) {
        Map<String, Object> aparcamientoMasCercano = null;
        double menorDistancia = Double.MAX_VALUE;

        for (Map<String, Object> aparcamiento : aparcamientos) {
            try {
                double latA = Double.parseDouble(aparcamiento.get("latitude").toString());
                double lonA = Double.parseDouble(aparcamiento.get("longitude").toString());
                int bikesAvailable = Integer.parseInt(aparcamiento.get("bikesAvailable").toString());

                if (bikesAvailable > 0) {
                    double distancia = calcularDistancia(lat, lon, latA, lonA);
                    if (distancia < menorDistancia) {
                        menorDistancia = distancia;
                        aparcamientoMasCercano = aparcamiento;
                    }
                }
            } catch (Exception e) {
                System.out.println("❌ Error procesando aparcamiento: " + e.getMessage());
            }
        }

        return aparcamientoMasCercano;
    }
}
