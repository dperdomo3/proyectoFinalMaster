package com.bikes.domain;

public class ParkingSnapshot {
    private Long id;
    private String direction;
    private int bikesAvailable;

    public ParkingSnapshot() {}

    public ParkingSnapshot(Long id, String direction, int bikesAvailable) {
        this.id = id;
        this.direction = direction;
        this.bikesAvailable = bikesAvailable;
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

    public int getBikesAvailable() {
        return bikesAvailable;
    }

    public void setBikesAvailable(int bikesAvailable) {
        this.bikesAvailable = bikesAvailable;
    }
}
