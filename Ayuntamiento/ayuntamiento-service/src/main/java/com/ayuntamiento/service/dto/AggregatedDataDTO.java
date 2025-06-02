package com.ayuntamiento.service.dto;

import java.time.Instant;
import java.util.List;

public class AggregatedDataDTO {
    private Instant timeStamp;
    private List<EstacionAparcamientoData> aggregatedData;

    // Getters y Setters
    public Instant getTimeStamp() {
        return timeStamp;
    }
    public void setTimeStamp(Instant timeStamp) {
        this.timeStamp = timeStamp;
    }
    public List<EstacionAparcamientoData> getAggregatedData() {
        return aggregatedData;
    }
    public void setAggregatedData(List<EstacionAparcamientoData> aggregatedData) {
        this.aggregatedData = aggregatedData;
    }

}

