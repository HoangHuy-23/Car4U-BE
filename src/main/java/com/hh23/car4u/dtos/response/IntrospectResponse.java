package com.hh23.car4u.dtos.response;

import lombok.Builder;

@Builder
public record IntrospectResponse(
        Boolean valid
) {
}
