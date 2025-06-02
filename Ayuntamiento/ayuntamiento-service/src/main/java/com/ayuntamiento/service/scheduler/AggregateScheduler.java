package com.ayuntamiento.service.scheduler;

import com.ayuntamiento.service.dto.AggregatedDataDTO;
import com.ayuntamiento.service.service.AccesoRemotoService;
import com.ayuntamiento.service.service.IntegracionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AggregateScheduler {

    @Autowired
    private IntegracionService integracionService;

    @Autowired
    private AccesoRemotoService accesoRemotoService;

    @Scheduled(fixedRateString = "${agg.interval:120000}") // cada 2 minutos por defecto
    public void ejecutarTareaProgramada() {
        System.out.println("🕒 Ejecutando tarea programada de agregación...");

        try {
            AggregatedDataDTO dto = integracionService.obtenerDatosAgregados();

            if (dto == null || dto.getAggregatedData() == null || dto.getAggregatedData().isEmpty()) {
                System.out.println("⚠️ No hay datos agregados para guardar.");
                return;
            }

            accesoRemotoService.guardarDatos(dto.getAggregatedData());

        } catch (Exception e) {
            System.err.println("❌ Error en tarea programada de agregación: " + e.getMessage());
        }
    }
}
