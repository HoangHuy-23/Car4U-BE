package com.hh23.car4u.dtos.response;


import com.hh23.car4u.entities.DriverLicense;
import com.hh23.car4u.entities.UserAddress;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Builder
public record UserResponse(
        String id,
        String name,
        LocalDate dob,
        String email,
        String phone,
        String gender,
        List<String> roles,
        Double rating,
        String avatar,
        Integer numOfTrips,
        boolean isActive,
        String googleAccountId,
        String facebookAccountId,
        DriverLicense driverLicense,
        List<UserAddress> addresses, // Address of the user
        List<String> myCars, // List of car IDs owned by the user
        List<String> myFavorites, // List of favorite car IDs
        Instant createdAt,
        Instant lastModifiedAt
) {
}
