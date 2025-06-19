package com.hh23.car4u.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hh23.car4u.entities.enums.UserAddressType;

import java.util.List;

public record UserAddressRequest(
        String id,
        String reminder,
        @JsonProperty(value = "isDefault")
        boolean isDefault,
        String no,
        String street,
        String ward,
        String district,
        String city,
        List<Double> coordinates,
        UserAddressType type
) {
}
