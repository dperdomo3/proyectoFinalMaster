package com.bikes.domain;

import java.time.Instant;

public class Parking {

    private Long id;
    private String direction;
    private int bikesCapacity;
    private float latitude;
    private float longitude;
    private int bikesAvailable;
    private int freeParkingSpots;
    private Instant createdAt;

    public Parking() {}

    public Parking(Long id, String direction, int bikesCapacity, float latitude, float longitude, int bikesAvailable, int freeParkingSpots, Instant createdAt) {
        this.id = id;
        this.direction = direction;
        this.bikesCapacity = bikesCapacity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.bikesAvailable = bikesAvailable;
        this.freeParkingSpots = freeParkingSpots;
        this.createdAt = createdAt;;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getBikesCapacity() {
        return bikesCapacity;
    }

    public void setBikesCapacity(int bikesCapacity) {
        this.bikesCapacity = bikesCapacity;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
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
    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Parking{" +
                "id='" + id + '\'' +
                ", direction='" + direction + '\'' +
                ", bikesCapacity=" + bikesCapacity +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", bikesAvailable=" + bikesAvailable +
                ", freeParkingSpots=" + freeParkingSpots +
                ", createdAt=" + createdAt +
                '}';
    }
}
