package com.hh23.car4u.controllers;

import com.hh23.car4u.dtos.ApiResponse;
import com.hh23.car4u.dtos.request.RentalContactRequest;
import com.hh23.car4u.dtos.response.RentalContactResponse;
import com.hh23.car4u.entities.RentalContact;
import com.hh23.car4u.repositories.RentalContactRepository;
import com.hh23.car4u.services.RentalContactService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rental-contacts")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RentalContactController {
    RentalContactService rentalContactService;
    private final RentalContactRepository rentalContactRepository;

    @PostMapping
    public ApiResponse<RentalContactResponse> createRentalContact(@RequestBody RentalContactRequest rentalContactRequest) {
        log.info("Creating rental contact: {}", rentalContactRequest);
        RentalContactResponse response = rentalContactService.createRentalContact(rentalContactRequest);
        return ApiResponse.<RentalContactResponse>builder()
                .message("Rental contact created successfully")
                .data(response)
                .build();
    }

    @PostMapping("/{rentalContactId}/accept")
    public ApiResponse<RentalContactResponse> acceptContact(@PathVariable String rentalContactId) {
        log.info("Accepting rental contact with ID: {}", rentalContactId);
        RentalContactResponse response = rentalContactService.acceptRentalContact(rentalContactId);
        return ApiResponse.<RentalContactResponse>builder()
                .message("Rental contact accepted successfully")
                .data(response)
                .build();
    }

    @PostMapping("/{rentalContactId}/reject")
    public ApiResponse<RentalContactResponse> rejectContact(@PathVariable String rentalContactId) {
        log.info("Rejecting rental contact with ID: {}", rentalContactId);
        RentalContactResponse response = rentalContactService.rejectRentalContact(rentalContactId);
        return ApiResponse.<RentalContactResponse>builder()
                .message("Rental contact rejected successfully")
                .data(response)
                .build();
    }

    @PostMapping("/{rentalContactId}/cancel")
    public ApiResponse<RentalContactResponse> cancelContact(@PathVariable String rentalContactId) {
        log.info("Canceling rental contact with ID: {}", rentalContactId);
        RentalContactResponse response = rentalContactService.cancelRentalContact(rentalContactId);
        return ApiResponse.<RentalContactResponse>builder()
                .message("Rental contact canceled successfully")
                .data(response)
                .build();
    }

    @PostMapping("/{rentalContactId}/complete")
    public ApiResponse<RentalContactResponse> completeContact(@PathVariable String rentalContactId) {
        log.info("Completing rental contact with ID: {}", rentalContactId);
        RentalContactResponse response = rentalContactService.completeRentalContact(rentalContactId);
        return ApiResponse.<RentalContactResponse>builder()
                .message("Rental contact completed successfully")
                .data(response)
                .build();
    }

    @GetMapping("/renter")
    public ApiResponse<List<RentalContactResponse>> getRentalContactsByRenter() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Getting rental contacts for renter with ID: {}", userId);
        List<RentalContactResponse> responses = rentalContactService.getRentalContactsByRenter();
        return ApiResponse.<List<RentalContactResponse>>builder()
                .message("Rental contacts retrieved successfully")
                .data(responses)
                .build();
    }

    @GetMapping("/owner")
    public ApiResponse<List<RentalContactResponse>> getRentalContactsByOwner() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Getting rental contacts for owner with ID: {}", userId);
        List<RentalContactResponse> responses = rentalContactService.getRentalContactsByOwner();
        return ApiResponse.<List<RentalContactResponse>>builder()
                .message("Rental contacts retrieved successfully")
                .data(responses)
                .build();
    }

}
