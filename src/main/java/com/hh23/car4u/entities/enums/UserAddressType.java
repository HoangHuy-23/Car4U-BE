package com.hh23.car4u.entities.enums;

import lombok.Getter;

@Getter
public enum UserAddressType {
    HOME("home"),
    OFFICE("office"),
    OTHER("other");

    private final String value;
    UserAddressType(String value) {
        this.value = value;
    }
}
