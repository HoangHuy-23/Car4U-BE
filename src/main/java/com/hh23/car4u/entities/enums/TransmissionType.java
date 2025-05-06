package com.hh23.car4u.entities.enums;

import lombok.Getter;

@Getter
public enum TransmissionType {
    MANUAL("Manual"),
    AUTOMATIC("Automatic"),;

    private final String value;

    TransmissionType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
