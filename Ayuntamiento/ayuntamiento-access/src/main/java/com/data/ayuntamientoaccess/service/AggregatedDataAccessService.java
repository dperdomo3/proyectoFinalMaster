package com.data.ayuntamientoaccess.service;

import com.data.ayuntamientoaccess.model.AggregatedDataDocument;
import com.data.ayuntamientoaccess.repository.AggregatedDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AggregatedDataAccessService {

    @Autowired
    private AggregatedDataRepository repository;

    public AggregatedDataDocument guardar(AggregatedDataDocument doc) {
        return repository.save(doc);
    }

    public AggregatedDataDocument obtenerUltimo() {
        return repository.findTopByOrderByTimeStampDesc();
    }
}
