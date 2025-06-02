package com.data.ayuntamientoaccess.repository;

import com.data.ayuntamientoaccess.model.AggregatedDataDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AggregatedDataRepository extends MongoRepository<AggregatedDataDocument, String> {

    AggregatedDataDocument findTopByOrderByTimeStampDesc();
}
