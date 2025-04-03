package com.hh23.car4u.dtos.response;


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
        List<String> roles,
        Double rating,
        String avatar,
        Integer numOfTrips,
        boolean isActive,
        Instant createdAt,
        Instant lastModifiedAt
) {
}
