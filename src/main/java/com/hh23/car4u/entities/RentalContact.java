package com.hh23.car4u.entities;

import com.hh23.car4u.entities.enums.ContactStatus;
import com.hh23.car4u.entities.enums.PickupLocation;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@Document(collection = "rental_contacts")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RentalContact {
    @MongoId
    ObjectId id;
    ObjectId renter;
    ObjectId car;
    ObjectId owner; // Owner of the car
    LocalDateTime pickupDate;
    LocalDateTime returnDate;
    PickupLocation pickupLocation;
    Double rentalFee;
    Double vat;
    Double totalFee;
    Double depositFee; // VD: 1000000
    Double collateralAmount;
    ContactStatus status; // VD: PENDING, CANCELLED, COMPLETED, ACCEPTED, REJECTED
    @CreatedDate
    @Setter(AccessLevel.NONE)
    Instant createdAt;
}
