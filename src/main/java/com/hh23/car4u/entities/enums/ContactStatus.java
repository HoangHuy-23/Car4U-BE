package com.hh23.car4u.entities.enums;

import lombok.Getter;

@Getter
public enum ContactStatus {
    PENDING("PENDING"),
    CANCELLED("CANCELLED"),
    ACCEPTED("ACCEPTED"),
    COMPLETED("COMPLETED"),
    REJECTED("REJECTED");

    private final String value;

    ContactStatus(String value) {
        this.value = value;
    }

    public static ContactStatus fromValue(String value) {
        for (ContactStatus status : ContactStatus.values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown contact status: " + value);
    }
}
