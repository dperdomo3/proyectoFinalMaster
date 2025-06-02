package com.pollution.controller;

import com.pollution.config.RoleValidator;
import com.pollution.dto.ApiResponse;
import com.pollution.dto.EstacionDTO;
import com.pollution.dto.LecturaDTO;
import com.pollution.service.ExternalPollutionAccessService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class EstacionController {

    @Autowired
    private ExternalPollutionAccessService service;

    @Autowired
    private RoleValidator roleValidator;

    @Autowired
    private HttpServletRequest request;

    /**
     * Endpoint protegido para crear una estación. Solo accesible por usuarios con rol ADMIN.
     */
    @PostMapping("/estacion")
    public ResponseEntity<ApiResponse<Void>> crearEstacion(@RequestBody EstacionDTO estacion) {
        if (!roleValidator.hasRole(request, "ADMIN")) {
            return ResponseEntity.status(403).body(new ApiResponse<>(false, "Acceso denegado", null));
        }

        service.crearEstacion(estacion);
        return ResponseEntity.ok(new ApiResponse<>(true, "Estación creada con éxito", null));
    }

    /**
     * Endpoint protegido para eliminar una estación. Solo accesible por ADMIN.
     */
    @DeleteMapping("/estacion/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarEstacion(@PathVariable Long id) {
        if (!roleValidator.hasRole(request, "ADMIN")) {
            return ResponseEntity.status(403).body(new ApiResponse<>(false, "Acceso denegado", null));
        }

        service.eliminarEstacion(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Estación eliminada correctamente", null));
    }

    /**
     * Endpoint público para obtener todas las estaciones.
     */
    @GetMapping("/estaciones")
    public ResponseEntity<ApiResponse<EstacionDTO[]>> obtenerEstaciones() {
        EstacionDTO[] estaciones = service.obtenerEstaciones();
        return ResponseEntity.ok(new ApiResponse<>(true, "Estaciones obtenidas correctamente", estaciones));
    }

    /**
     * Registro de lectura para una estación. Solo usuarios con rol ESTACION pueden usar este endpoint.
     */
    @PostMapping("/estacion/{id}")
    public ResponseEntity<ApiResponse<Void>> registrarLectura(@PathVariable Long id, @RequestBody LecturaDTO lectura) {
        if (!roleValidator.hasRole(request, "ESTACION")) {
            return ResponseEntity.status(403).body(new ApiResponse<>(false, "Acceso denegado", null));
        }

        service.registrarLectura(id, lectura);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lectura registrada con éxito", null));
    }

    /**
     * Obtener la última lectura de una estación (ordenada por timestamp). Público.
     */
    @GetMapping("/estacion/{id}/status")
    public ResponseEntity<ApiResponse<LecturaDTO>> obtenerUltimaLectura(@PathVariable Long id) {
        LecturaDTO ultima = service.obtenerUltimaLectura(id);
        if (ultima == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>(false, "No se encontraron lecturas", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Última lectura obtenida con éxito", ultima));
    }

    /**
     * Obtener lecturas de una estación en un intervalo de tiempo. Público.
     */
    @GetMapping(value = "/estacion/{id}/status", params = {"from", "to"})
    public ResponseEntity<ApiResponse<LecturaDTO[]>> obtenerLecturasEnIntervalo(
            @PathVariable Long id,
            @RequestParam String from,
            @RequestParam String to
    ) {
        LecturaDTO[] lecturas = service.obtenerLecturasEnIntervalo(id, from, to);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lecturas en intervalo obtenidas con éxito", lecturas));
    }
}
