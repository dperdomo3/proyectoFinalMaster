package com.data.ayuntamientoaccess.model;

import com.data.ayuntamientoaccess.dto.EstacionAparcamientoData;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "aggregated_data")
public class AggregatedDataDocument {

    @Id
    private String id;
    private Instant timeStamp;
    private List<EstacionAparcamientoData> aggregatedData;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Instant getTimeStamp() { return timeStamp; }
    public void setTimeStamp(Instant timeStamp) { this.timeStamp = timeStamp; }

    public List<EstacionAparcamientoData> getAggregatedData() { return aggregatedData; }
    public void setAggregatedData(List<EstacionAparcamientoData> aggregatedData) { this.aggregatedData = aggregatedData; }
}
