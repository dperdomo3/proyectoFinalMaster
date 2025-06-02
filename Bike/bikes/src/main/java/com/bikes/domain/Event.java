package com.bikes.domain;

import java.time.Instant;

public class Event {

    private String id;
    private String parkingId;
    private String operation; // aparcamiento, alquiler, reposición_múltiple, retirada_múltiple
    private int quantity;
    private int bikesAvailable;
    private int freeParkingSpots;
    private Instant timestamp;

    public Event() {}

    public Event(String id, String parkingId, String operation, int bikesAvailable, int freeParkingSpots, Instant timestamp, int quantity) {
        this.id = id;
        this.parkingId = parkingId;
        this.operation = operation;
        this.bikesAvailable = bikesAvailable;
        this.freeParkingSpots = freeParkingSpots;
        this.timestamp = timestamp;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParkingId() {
        return parkingId;
    }

    public void setParkingId(String parkingId) {
        this.parkingId = parkingId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
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

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", parkingId='" + parkingId + '\'' +
                ", operation='" + operation + '\'' +
                ", bikesAvailable=" + bikesAvailable +
                ", freeParkingSpots=" + freeParkingSpots +
                ", timestamp=" + timestamp +
                ", quantity=" + quantity +
                '}';
    }
}
