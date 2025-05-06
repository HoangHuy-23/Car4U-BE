package com.hh23.car4u.entities.enums;

import lombok.Getter;

@Getter
public enum CarStatus {
    AVAILABLE("Available"),
    BOOKED("Booked"),
    MAINTENANCE("Maintenance"),
    UNAVAILABLE("Unavailable");

    private final String status;

    CarStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}
