package com.hh23.car4u.dtos.request;

import com.hh23.car4u.entities.enums.CarFeature;
import com.hh23.car4u.entities.enums.CarType;
import com.hh23.car4u.entities.enums.FuelType;
import com.hh23.car4u.entities.enums.TransmissionType;
import lombok.Builder;

import java.util.List;

@Builder
public record CarFilterRequest(
//        String keyword,
        String brand,
        String model,
        String color,
        String year,
        FuelType fuelType,
        Double fuelConsumption,
        TransmissionType transmissionType,
        CarType type,
        Double minPrice,
        Double maxPrice,
        Integer minSeats,
        Integer maxSeats,
        Double rating,
        String features,
        String location,
        Boolean deliveryAvailable,
        String sortBy
//        Integer page,
//        Integer size,
//        String startDate,                 // Thêm để tìm xe rảnh
//        String endDate
) {
}
