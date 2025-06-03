package com.ayuntamiento.service.service;

import com.ayuntamiento.service.dto.AggregatedDataDTO;
import com.ayuntamiento.service.dto.EstacionAparcamientoData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;

@Service
public class AccesoRemotoService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String BASE_URL = "http://ayuntamiento-access/aggregated-data"; // Ajusta el puerto si es necesario

    public void guardarDatos(List<EstacionAparcamientoData> data) {
        AggregatedDataDTO dto = new AggregatedDataDTO();
        dto.setTimeStamp(Instant.now());
        dto.setAggregatedData(data);
        restTemplate.postForEntity(BASE_URL, dto, AggregatedDataDTO.class);
    }

    public AggregatedDataDTO obtenerUltimo() {
        return restTemplate.getForObject(BASE_URL + "/latest", AggregatedDataDTO.class);
    }
}
