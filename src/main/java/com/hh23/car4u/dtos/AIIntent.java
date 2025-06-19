package com.hh23.car4u.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hh23.car4u.dtos.request.CarFilterRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AIIntent {
    private String intent; // VD: "greeting", "car_search", "rental_request"
    private CarFilterRequest carFilter;
    private String message; // VD: "Hello, how can I help you?", "I want to rent a car", "What cars are available?"
}
