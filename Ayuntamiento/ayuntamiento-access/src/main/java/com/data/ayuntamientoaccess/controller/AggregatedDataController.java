package com.data.ayuntamientoaccess.controller;

import com.data.ayuntamientoaccess.model.AggregatedDataDocument;
import com.data.ayuntamientoaccess.service.AggregatedDataAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/aggregated-data")
public class AggregatedDataController {

    @Autowired
    private AggregatedDataAccessService service;

    @PostMapping
    public ResponseEntity<AggregatedDataDocument> guardar(@RequestBody AggregatedDataDocument doc) {
        AggregatedDataDocument saved = service.guardar(doc);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/latest")
    public ResponseEntity<AggregatedDataDocument> obtenerUltimo() {
        AggregatedDataDocument latest = service.obtenerUltimo();
        return ResponseEntity.ok(latest);
    }
}

