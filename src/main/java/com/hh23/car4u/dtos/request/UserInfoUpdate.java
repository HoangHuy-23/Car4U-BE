package com.hh23.car4u.dtos.request;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record UserInfoUpdate(
        String name,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate dob,
        String gender
) {
}
