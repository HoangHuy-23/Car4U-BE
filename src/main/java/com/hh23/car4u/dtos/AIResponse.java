package com.hh23.car4u.dtos;

import com.hh23.car4u.dtos.response.CarResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AIResponse {
    PageResponse<CarResponse> results;
    String message;
    String intent;
}