package com.data.access.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "events")
public class Event {

    @Id
    private String id;
    private Long parkingId; // asegúrate que este sea Long también si cambiaste el ID de Parking
    private String operation; // aparcamiento, alquiler, reposicion_multiple, retirada_multiple
    private int quantity;     // número de bicis (1 por defecto si no es múltiple)
    private Instant timestamp;
    private int bikesAvailable;
    private int freeParkingSpots;

    public Event() {
    }

    public Event(String id, Long parkingId, String operation, int quantity, 
    Instant timestamp, int bikesAvailable, int freeParkingSpots) {
        this.id= id;
        this.parkingId = parkingId;
        this.operation = operation;
        this.quantity = quantity;
        this.timestamp = timestamp;
        this.bikesAvailable = bikesAvailable;
        this.freeParkingSpots = freeParkingSpots;
    }

    public String  getId() {
        return id;
    }

    public void setId(String  id) {
        this.id = id;
    }

    public Long getParkingId() {
        return parkingId;
    }

    public void setParkingId(Long parkingId) {
        this.parkingId = parkingId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    public int getBikesAvailable() {
        return bikesAvailable;
    }
    public void setBikesAvailable(int bikesAvailable) {
        this.bikesAvailable = bikesAvailable;
    }
    public int getFreeParkingSpots() {
        return freeParkingSpots;
    }
    public void setFreeParkingSpots(int freeParkingSpots) {
        this.freeParkingSpots = freeParkingSpots;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", parkingId=" + parkingId +
                ", operation='" + operation + '\'' +
                ", quantity=" + quantity +
                ", timestamp=" + timestamp +
                ", bikesAvailable=" + bikesAvailable +
                ", freeParkingSpots=" + freeParkingSpots +
                '}';
    }
}
