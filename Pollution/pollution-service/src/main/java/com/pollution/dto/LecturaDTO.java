package com.pollution.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LecturaDTO {
    private Long estacionId;
    private Double nitricOxides;
    private Double nitrogenDioxides;


    @JsonProperty("VOCs_NMHC")
    private Double vocsNMHC;

    @JsonProperty("PM2_5")
    private Double pm25;

    // Getters y setters
    public Long getEstacionId() { return estacionId; }
    public void setEstacionId(Long estacionId) { this.estacionId = estacionId; }

    public Double getNitricOxides() { return nitricOxides; }
    public void setNitricOxides(Double nitricOxides) { this.nitricOxides = nitricOxides; }

    public Double getNitrogenDioxides() { return nitrogenDioxides; }
    public void setNitrogenDioxides(Double nitrogenDioxides) { this.nitrogenDioxides = nitrogenDioxides; }

    public Double getVocsNMHC() { return vocsNMHC; }
    public void setVocsNMHC(Double vocsNMHC) { this.vocsNMHC = vocsNMHC; }

    public Double getPm25() { return pm25; }
    public void setPm25(Double pm25) { this.pm25 = pm25; }
}

