package com.data.ayuntamientoaccess.dto;

public class EstacionAparcamientoData {
    private Long id;
    private double average_bikesAvailable;
    private AirQualityDTO air_quality;

    // Getters y Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public double getAverage_bikesAvailable() {
        return average_bikesAvailable;
    }
    public void setAverage_bikesAvailable(double average_bikesAvailable) {
        this.average_bikesAvailable = average_bikesAvailable;
    }
    public AirQualityDTO getAir_quality() {
        return air_quality;
    }
    public void setAir_quality(AirQualityDTO air_quality) {
        this.air_quality = air_quality;
    }
}

