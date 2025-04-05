package com.hh23.car4u.entities.enums;

import lombok.Getter;

@Getter
public enum CarFeature {
    GPS("GPS Navigation"),
    BLUETOOTH("Bluetooth"),
    CAMERA_360("360 Camera"),
    LATERAL_CAMERA("Lateral Camera"),
    DASH_CAM("Dash Camera"),
    REAR_CAMERA("Rear Camera"),
    TIRE_SENSORS("Tire Sensors"),
    COLLISION_SENSORS("Collision Sensors"),
    SPEED_ALERT("Speed Alert"),
    USB_PORT("USB Port"),
    SPARE_TIRE("Spare Tire"),
    DVD_SCREEN("DVD Screen"),
    AIRBAG("Safety Airbags");

    private final String description;

    CarFeature(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
