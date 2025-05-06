package com.hh23.car4u.entities.enums;

import lombok.Getter;

@Getter
public enum CarType {
    SEDAN("Sedan"),
    MPV("MPV"),
    CUV("CUV"),
    SUV("SUV"),
    HATCHBACK("Hatchback"),
    PICKUP("Pickup"),
    SPORTS_CAR("Sports Car"),
    LUXURY("Luxury");

    private final String displayName;

    CarType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
