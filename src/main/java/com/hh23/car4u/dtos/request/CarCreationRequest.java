package com.hh23.car4u.dtos.request;

import com.hh23.car4u.entities.CarDeliveryPolicy;
import com.hh23.car4u.entities.CarLocation;
import com.hh23.car4u.entities.enums.CarFeature;
import com.hh23.car4u.entities.enums.CarType;
import com.hh23.car4u.entities.enums.FuelType;
import com.hh23.car4u.entities.enums.TransmissionType;

import java.util.List;

public record CarCreationRequest(
        String ownerId,                    // ID of the owner (for user)
        String name,                       // Name of the car
        String brand,                      // Car brand
        String model,                      // Car model
        String color,                      // Car color
        String year,                       // Manufacture year
        String licensePlate,               // License plate
        String vin,                        // Vehicle Identification Number (VIN)
        FuelType fuelType,                 // Fuel type
        Double fuelConsumption,              // Fuel consumption (L/100km)
        TransmissionType transmissionType, // Transmission type (automatic, manual)
        CarType type,                      // Car type (SUV, sedan, etc.)
        Integer numOfSeats,                // Number of seats
        List<String> images,                    // List of image URLs
        Double pricePerDay,                // Price per day for renting
        List<CarFeature> features,         // List of car features (e.g., Bluetooth, GPS)
        String description,                // Description of the car
        CarLocation location,              // Location of the car
        CarDeliveryPolicy deliveryPolicy,  // Delivery policy (e.g., home delivery)
        Boolean collateralRequired,        // Whether collateral is required
        Double collateralAmount           // Amount of collateral
) {
}
