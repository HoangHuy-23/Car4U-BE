package com.hh23.car4u.entities.enums;

import lombok.Getter;

@Getter
public enum FuelType {
    GASOLINE("Gasoline"),
    DIESEL("Diesel"),
    ELECTRIC("Electric"),
    HYBRID("Hybrid");

    private final String value;

    FuelType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
