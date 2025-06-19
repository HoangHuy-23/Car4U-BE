package com.hh23.car4u.dtos.response;

import com.hh23.car4u.entities.enums.ContactStatus;
import com.hh23.car4u.entities.enums.PickupLocation;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
public class RentalContactResponse{
        String id;
        CarResponse car;
        UserResponse renter;
        UserResponse owner;
        LocalDateTime pickupDate;
        LocalDateTime returnDate;
        PickupLocation pickupLocation;
        Double rentalFee;
        Double vat;
        Double totalFee;
        Double depositFee; // VD: 1000000
        Double collateralAmount;
        ContactStatus status; // VD: PENDING; CANCELLED; COMPLETED; ACCEPTED; REJECTED
        Instant createdAt;
}
