package com.hh23.car4u.controllers;

import com.hh23.car4u.dtos.ApiResponse;
import com.hh23.car4u.dtos.PageResponse;
import com.hh23.car4u.dtos.request.CarCreationRequest;
import com.hh23.car4u.dtos.request.CarFilterRequest;
import com.hh23.car4u.dtos.response.CarResponse;
import com.hh23.car4u.dtos.response.UserResponse;
import com.hh23.car4u.entities.enums.CarFeature;
import com.hh23.car4u.entities.enums.CarType;
import com.hh23.car4u.entities.enums.FuelType;
import com.hh23.car4u.entities.enums.TransmissionType;
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

    @GetMapping("/external/{carId}")
    ApiResponse<CarResponse> getCar(@PathVariable String carId) {
        log.info("Get car with id: {}", carId);
        var response = carService.getCar(carId);
        return ApiResponse.<CarResponse>builder()
                .message("Get car successfully")
                .data(response)
                .build();
    }

    @GetMapping("/external/{carId}/owner")
    ApiResponse<UserResponse> getCarOwner(@PathVariable String carId) {
        log.info("Get car owner with id: {}", carId);
        var response = carService.getCarOwner(carId);
        return ApiResponse.<UserResponse>builder()
                .message("Get car owner successfully")
                .data(response)
                .build();
    }

    @GetMapping("/filter")
    ApiResponse<PageResponse<CarResponse>> filterCars(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam() String location,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String fuelType,
            @RequestParam(required = false) String transmissionType,
            @RequestParam(required = false) Double fuelConsumption,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer minSeats,
            @RequestParam(required = false) Integer maxSeats,
            @RequestParam(required = false) Double rating,
            @RequestParam(required = false) String features,
            @RequestParam(required = false) Boolean deliveryAvailable,
            @RequestParam(required = false) String sortBy
            ) {
        log.info("Filter cars with request: pageNo={}, location={}, brand={}, model={}, color={}, year={}, fuelType={}, transmissionType={}, fuelConsumption={}, type={}, minPrice={}, maxPrice={}, minSeats={}, maxSeats={}, rating={}, features={}, deliveryAvailable={}, sortBy={}",
                pageNo, location, brand, model, color, year, fuelType, transmissionType, fuelConsumption, type, minPrice, maxPrice, minSeats, maxSeats, rating, features, deliveryAvailable, sortBy);
        var request = CarFilterRequest.builder()
                .brand(brand)
                .model(model)
                .color(color)
                .year(year)
                .fuelType(fuelType != null ? FuelType.valueOf(fuelType) : null)
                .fuelConsumption(fuelConsumption)
                .transmissionType(transmissionType != null ? TransmissionType.valueOf(transmissionType) : null)
                .type(type != null ? CarType.valueOf(type) : null)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .minSeats(minSeats)
                .maxSeats(maxSeats)
                .rating(rating)
                .features(features)
                .deliveryAvailable(deliveryAvailable)
                .location(location)
                .sortBy(sortBy)
                .build();
        var response = carService.filterCar(pageNo, request);
        return ApiResponse.<PageResponse<CarResponse>>builder()
                .message("Filter cars successfully")
                .data(response)
                .build();
    }

}
