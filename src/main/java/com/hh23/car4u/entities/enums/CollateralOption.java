package com.hh23.car4u.entities.enums;

import lombok.Getter;

@Getter
public enum CollateralOption {
    NONE("None"),
    CASH("Cash"),
    MOTORBIKE_ASSET("Motorbike");

    private final String description;

    CollateralOption(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
