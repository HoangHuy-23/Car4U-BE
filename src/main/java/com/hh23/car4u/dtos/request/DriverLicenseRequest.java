package com.hh23.car4u.dtos.request;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;

public record DriverLicenseRequest(
        MultipartFile file,
        String licenseNumber,
        String name,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate dob
        ) {
}
