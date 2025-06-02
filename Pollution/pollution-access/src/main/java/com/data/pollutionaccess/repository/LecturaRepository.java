package com.data.pollutionaccess.repository;

import com.data.pollutionaccess.model.Lectura;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface LecturaRepository extends MongoRepository<Lectura, String> {
    List<Lectura> findByEstacionIdAndTimestampBetween(String estacionId, Instant from, Instant to);
    List<Lectura> findByEstacionIdOrderByTimestampDesc(String estacionId);

}
