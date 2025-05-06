package com.hh23.car4u.dtos.response;

import com.hh23.car4u.entities.CarDeliveryPolicy;
import com.hh23.car4u.entities.CarLocation;
import com.hh23.car4u.entities.enums.*;

import java.util.List;

public record CarResponse(
        String id,
        String ownerId,
        String name,
        String brand,
        String model,
        String color,
        String year,
        String licensePlate,
        String vin,
        FuelType fuelType,
        Double fuelConsumption,
        TransmissionType transmissionType,
        CarType type,
        Integer numOfSeats,
        Integer numOfTrips,
        List<String> images,
        Double pricePerDay,
        CarStatus status,
        CarLocation location,
        String description,
        List<CarFeature> features,
        Double rating,
        Integer numOfRatings,
        CarDeliveryPolicy deliveryPolicy,
        Boolean collateralRequired,
        Double collateralAmount,
        Boolean isVerified,
        String verificationNote,
        Boolean isDeleted
) {
}
