package com.hh23.car4u.services;

import com.hh23.car4u.dtos.request.RentalContactRequest;
import com.hh23.car4u.dtos.response.RentalContactResponse;
import com.hh23.car4u.entities.enums.ContactStatus;

import java.util.List;

public interface RentalContactService {
    RentalContactResponse createRentalContact(RentalContactRequest request);
    RentalContactResponse updateStatus(String rentalContactId, ContactStatus status);
    RentalContactResponse acceptRentalContact(String rentalContactId);
    RentalContactResponse rejectRentalContact(String rentalContactId);
    RentalContactResponse cancelRentalContact(String rentalContactId);
    RentalContactResponse completeRentalContact(String rentalContactId);
    List<RentalContactResponse> getRentalContactsByRenter();
    List<RentalContactResponse> getRentalContactsByOwner();
}
