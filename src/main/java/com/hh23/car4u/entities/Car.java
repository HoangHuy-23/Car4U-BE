package com.hh23.car4u.entities;

import com.hh23.car4u.entities.enums.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@Setter
@Getter
@Builder
@Document(collection = "cars")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Car {
    @MongoId
    ObjectId id;
//  owner
    String ownerId;
//  car details
    String name;
    String brand;
    String model;
    String color;
    String year;
    String licensePlate;
    String vin; // Vehicle Identification Number
    FuelType fuelType;
    Double fuelConsumption; // L/100km
    TransmissionType transmissionType;
    CarType type;
    Integer numOfSeats;
//  number of trips
    Integer numOfTrips;
//  car images
    List<String> images;
//  price details
    Double pricePerDay;
//  car state
    CarStatus status;
//  car location
    CarLocation location;
//  car description
    String description;
//  car features
    List<CarFeature> features;
//  car rating
    Double rating;
    Integer numOfRatings;
//  car delivery
    CarDeliveryPolicy deliveryPolicy;
//  collateral
    Boolean collateralRequired;
    Double collateralAmount;
//  car verification
    Boolean isVerified; // Admin đã duyệt xe chưa?
    String verificationNote; // Ghi chú nếu bị từ chối duyệt

    Boolean isDeleted;
    //  car created and last modified date
    @Setter(AccessLevel.NONE)
    @CreatedDate
    String createdAt;
    @Setter(AccessLevel.NONE)
    @LastModifiedDate
    String lastModifiedAt;
}
