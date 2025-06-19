package com.hh23.car4u.dtos.request;

import com.hh23.car4u.entities.enums.ContactStatus;
import com.hh23.car4u.entities.enums.PickupLocation;

import java.time.LocalDateTime;

public record RentalContactRequest(
        String carId,
        String renterId,
        String ownerId,
        LocalDateTime pickupDate,
        LocalDateTime returnDate,
        PickupLocation pickupLocation,
        Double rentalFee,
        Double vat,
        Double totalFee,
        Double depositFee, // VD: 1000000
        Double collateralAmount,
        ContactStatus status // VD: PENDING, CANCELLED, COMPLETED, ACCEPTED, REJECTED
) { }
