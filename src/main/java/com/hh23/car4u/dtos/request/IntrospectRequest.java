package com.hh23.car4u.dtos.request;

import jakarta.validation.constraints.NotBlank;

public record IntrospectRequest(
        @NotBlank(message = "Access token is required")
        String accessToken
) {
}
