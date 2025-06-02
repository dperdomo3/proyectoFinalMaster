package com.data.pollutionaccess.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

@Document(collection = "lecturas")
public class Lectura {

    @Id
    private String id;

    private String estacionId;
    private Instant timestamp;
    private double nitricOxides;
    private double nitrogenDioxides;

    @JsonProperty("VOCs_NMHC")
    private double VOCs_NMHC;

    @JsonProperty("PM2_5")
    private double PM2_5;

    // Getters y setters

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEstacionId() { return estacionId; }
    public void setEstacionId(String estacionId) { this.estacionId = estacionId; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public double getNitricOxides() { return nitricOxides; }
    public void setNitricOxides(double nitricOxides) { this.nitricOxides = nitricOxides; }

    public double getNitrogenDioxides() { return nitrogenDioxides; }
    public void setNitrogenDioxides(double nitrogenDioxides) { this.nitrogenDioxides = nitrogenDioxides; }

    public double getVOCs_NMHC() { return VOCs_NMHC; }
    public void setVOCs_NMHC(double VOCs_NMHC) { this.VOCs_NMHC = VOCs_NMHC; }

    public double getPM2_5() { return PM2_5; }
    public void setPM2_5(double PM2_5) { this.PM2_5 = PM2_5; }
}