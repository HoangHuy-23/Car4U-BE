package com.hh23.car4u.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record LoginSocialRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        String email,
        @NotBlank(message = "Name is required")
        String name,
        @NotNull(message = "Date of birth is required")
        LocalDate dob,
        @NotBlank(message = "Phone number is required")
        @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 digits")
        String phone,
        String avatar,
        String googleAccountId,
        String facebookAccountId,
        String gender
) {
}
