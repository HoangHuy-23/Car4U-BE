package com.hh23.car4u.controllers;

import com.hh23.car4u.dtos.ApiResponse;
import com.hh23.car4u.dtos.request.CarCreationRequest;
import com.hh23.car4u.dtos.response.CarResponse;
import com.hh23.car4u.services.CarService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CarController {
    CarService carService;

    @PostMapping()
    ApiResponse<CarResponse> createCar(@RequestBody CarCreationRequest request) {
        log.info("Create car with request: {}", request);
        var response = carService.addCar(request, request.ownerId());
        return ApiResponse.<CarResponse>builder()
                .message("Create car successfully")
                .data(response)
                .build();
    }

    @GetMapping
    ApiResponse<List<CarResponse>> getAllCars() {
        log.info("Get all cars");
        var response = carService.getAllCars();
        return ApiResponse.<List<CarResponse>>builder()
                .message("Get all cars successfully")
                .data(response)
                .build();
    }
}
