package com.pollution.service;

import com.pollution.dto.EstacionDTO;
import com.pollution.dto.LecturaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalPollutionAccessService {

    @Autowired
    private RestTemplate restTemplate;

    // URL base del microservicio pollution-access
    private final String baseUrl = "http://pollution-access";

    /**
     * Crea una estación en el microservicio de acceso.
     */
    public void crearEstacion(EstacionDTO estacion) {
        restTemplate.postForEntity(baseUrl + "/estacion", estacion, Void.class);
    }

    /**
     * Elimina una estación en el microservicio de acceso.
     */
    public void eliminarEstacion(Long id) {
        restTemplate.delete(baseUrl + "/estacion/" + id);
    }

    /**
     * Obtiene todas las estaciones desde el microservicio de acceso.
     */
    public EstacionDTO[] obtenerEstaciones() {
        return restTemplate.getForObject(baseUrl + "/estacion", EstacionDTO[].class);
    }

    /**
     * Registra una lectura para una estación específica.
     */
    public void registrarLectura(Long id, LecturaDTO lectura) {
        lectura.setEstacionId(id); // Se asegura de asignar el ID de la estación
        restTemplate.postForEntity(baseUrl + "/lectura", lectura, Void.class);
    }

    /**
     * Obtiene la última lectura registrada para una estación.
     * Si no hay lecturas, devuelve null.
     */
    public LecturaDTO obtenerUltimaLectura(Long id) {
        try {
            return restTemplate.getForObject(baseUrl + "/lectura/" + id, LecturaDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            return null; // Devuelve null si no hay lecturas, para manejarlo en el controller
        }
    }

    /**
     * Obtiene las lecturas dentro de un intervalo para una estación.
     * Si no hay lecturas, devuelve un array vacío.
     */
    public LecturaDTO[] obtenerLecturasEnIntervalo(Long id, String from, String to) {
        String url = baseUrl + "/lectura/" + id + "?from=" + from + "&to=" + to;
        try {
            return restTemplate.getForObject(url, LecturaDTO[].class);
        } catch (HttpClientErrorException.NotFound e) {
            return new LecturaDTO[0]; // Devuelve un array vacío si no hay lecturas en el rango
        }
    }
}
