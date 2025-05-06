package com.hh23.car4u.entities.enums;

import lombok.Getter;

@Getter
public enum CarFeature {
    MAP("Map"),
    BLUETOOTH("Bluetooth"),
    CAMERA_360("360 Camera"),
    SIDE_CAMERA("Side Camera"),
    DASH_CAM("Dash Cam"),
    REVERSING_CAMERA("Reversing Camera"),
    TIRE_PRESSURE_SENSOR("Tire Pressure Sensor"),
    COLLISION_SENSOR("Collision Sensor"),
    SPEED_WARNING("Speed Warning"),
    SUNROOF("Sunroof"),
    GPS_NAVIGATION("GPS Navigation"),
    CHILD_SEAT("Child Seat"),
    USB_PORT("USB Port"),
    SPARE_TIRE("Spare Tire"),
    DVD_SCREEN("DVD Screen"),
    PICKUP_TRUCK_BED_COVER("Pickup Truck Bed Cover"),
    ETC("ETC (Electronic Toll Collection)"),
    AIRBAG("Airbag");

    private final String description;

    CarFeature(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
