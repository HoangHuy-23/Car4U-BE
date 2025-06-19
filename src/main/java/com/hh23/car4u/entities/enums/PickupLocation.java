package com.hh23.car4u.entities.enums;

import lombok.Getter;

@Getter
public enum PickupLocation {
    USER_LOCATION("User Location"),
    CAR_LOCATION("Car Location"),
    CUSTOM_LOCATION("Custom Location");
    private final String description;
    PickupLocation(String description) {
        this.description = description;
        }
}
