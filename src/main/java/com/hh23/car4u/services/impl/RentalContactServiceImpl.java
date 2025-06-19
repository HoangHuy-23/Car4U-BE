package com.hh23.car4u.services.impl;

import com.hh23.car4u.dtos.request.RentalContactRequest;
import com.hh23.car4u.dtos.response.CarResponse;
import com.hh23.car4u.dtos.response.RentalContactResponse;
import com.hh23.car4u.dtos.response.UserResponse;
import com.hh23.car4u.entities.RentalContact;
import com.hh23.car4u.entities.enums.ContactStatus;
import com.hh23.car4u.entities.enums.PickupLocation;
import com.hh23.car4u.exception.AppException;
import com.hh23.car4u.exception.ErrorCode;
import com.hh23.car4u.mappers.RentalContactMapper;
import com.hh23.car4u.repositories.CarRepository;
import com.hh23.car4u.repositories.RentalContactRepository;
import com.hh23.car4u.repositories.UserRepository;
import com.hh23.car4u.services.CarService;
import com.hh23.car4u.services.RentalContactService;
import com.hh23.car4u.services.UserService;
import com.hh23.car4u.utils.ObjectIdUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bson.types.ObjectId;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RentalContactServiceImpl implements RentalContactService {
    RentalContactRepository rentalContactRepository;
    RentalContactMapper rentalContactMapper;
    CarService carService;
    UserService userService;
    UserRepository userRepository;
    CarRepository carRepository;

    @Override
    public RentalContactResponse createRentalContact(RentalContactRequest request) {
        RentalContact rentalContact = rentalContactMapper.toEntity(request);
        ObjectId carId = ObjectIdUtil.toObjectId(request.carId());
        ObjectId userId = ObjectIdUtil.toObjectId(request.renterId());
        ObjectId ownerId = ObjectIdUtil.toObjectId(request.ownerId());
        rentalContact.setCar(carId);
        rentalContact.setRenter(userId);
        rentalContact.setStatus(ContactStatus.PENDING);
        rentalContact.setOwner(ownerId);
        rentalContact = rentalContactRepository.save(rentalContact);
        RentalContactResponse response = rentalContactMapper.toResponse(rentalContact);
        CarResponse carResponse = carService.getCar(request.carId());
        response.setCar(carResponse);
        UserResponse userResponse = userService.getUserById(request.renterId());
        response.setRenter(userResponse);
        response.setOwner(userService.getUserById(carResponse.ownerId()));
        return response;
    }

    @Override
    public RentalContactResponse updateStatus(String rentalContactId, ContactStatus status) {
        RentalContact rentalContact = rentalContactRepository.findById(ObjectIdUtil.toObjectId(rentalContactId))
                .orElseThrow(() -> new IllegalArgumentException("Rental contact not found"));
        rentalContact.setStatus(status);
        RentalContactResponse response = rentalContactMapper.toResponse(rentalContact);
        CarResponse carResponse = carService.getCar(rentalContact.getCar().toHexString());
        response.setCar(carResponse);
        UserResponse userResponse = userService.getUserById(rentalContact.getRenter().toHexString());
        response.setRenter(userResponse);
        response.setOwner(userService.getUserById(carResponse.ownerId()));
        rentalContactRepository.save(rentalContact);
        return response;
    }

    @Override
    public RentalContactResponse acceptRentalContact(String rentalContactId) {
        RentalContact rentalContact = rentalContactRepository.findById(ObjectIdUtil.toObjectId(rentalContactId))
                .orElseThrow(() -> new IllegalArgumentException("Rental contact not found"));
        rentalContact.setStatus(ContactStatus.ACCEPTED);
        rentalContactRepository.save(rentalContact);
        RentalContactResponse response = rentalContactMapper.toResponse(rentalContact);
        CarResponse carResponse = carService.getCar(rentalContact.getCar().toHexString());
        response.setCar(carResponse);
        UserResponse userResponse = userService.getUserById(rentalContact.getRenter().toHexString());
        response.setRenter(userResponse);
        response.setOwner(userService.getUserById(carResponse.ownerId()));
        return response;
    }

    @Override
    public RentalContactResponse rejectRentalContact(String rentalContactId) {
        RentalContact rentalContact = rentalContactRepository.findById(ObjectIdUtil.toObjectId(rentalContactId))
                .orElseThrow(() -> new IllegalArgumentException("Rental contact not found"));
        rentalContact.setStatus(ContactStatus.REJECTED);
        rentalContactRepository.save(rentalContact);
        RentalContactResponse response = rentalContactMapper.toResponse(rentalContact);
        CarResponse carResponse = carService.getCar(rentalContact.getCar().toHexString());
        response.setCar(carResponse);
        UserResponse userResponse = userService.getUserById(rentalContact.getRenter().toHexString());
        response.setRenter(userResponse);
        response.setOwner(userService.getUserById(carResponse.ownerId()));
        return response;
    }

    @Override
    public RentalContactResponse cancelRentalContact(String rentalContactId) {
        RentalContact rentalContact = rentalContactRepository.findById(ObjectIdUtil.toObjectId(rentalContactId))
                .orElseThrow(() -> new IllegalArgumentException("Rental contact not found"));
        rentalContact.setStatus(ContactStatus.CANCELLED);
        rentalContactRepository.save(rentalContact);
        RentalContactResponse response = rentalContactMapper.toResponse(rentalContact);
        CarResponse carResponse = carService.getCar(rentalContact.getCar().toHexString());
        response.setCar(carResponse);
        UserResponse userResponse = userService.getUserById(rentalContact.getRenter().toHexString());
        response.setRenter(userResponse);
        response.setOwner(userService.getUserById(carResponse.ownerId()));
        return response;
    }

    @Override
    public RentalContactResponse completeRentalContact(String rentalContactId) {
        RentalContact rentalContact = rentalContactRepository.findById(ObjectIdUtil.toObjectId(rentalContactId))
                .orElseThrow(() -> new IllegalArgumentException("Rental contact not found"));
        rentalContact.setStatus(ContactStatus.COMPLETED);
        rentalContactRepository.save(rentalContact);
        RentalContactResponse response = rentalContactMapper.toResponse(rentalContact);
        CarResponse carResponse = carService.getCar(rentalContact.getCar().toHexString());
        response.setCar(carResponse);
        UserResponse userResponse = userService.getUserById(rentalContact.getRenter().toHexString());
        response.setRenter(userResponse);
        response.setOwner(userService.getUserById(carResponse.ownerId()));
        return response;
    }

    @Override
    public List<RentalContactResponse> getRentalContactsByRenter() {
        var context = SecurityContextHolder.getContext();
        if (context.getAuthentication() == null || !context.getAuthentication().isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String userId = context.getAuthentication().getName();
        ObjectId objectUserId = ObjectIdUtil.toObjectId(userId);
        var user = userRepository.findById(objectUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        var listRentalContacts = rentalContactRepository.findByRenter(objectUserId);
        if (listRentalContacts.isEmpty()) {
            return List.of();
        }
        // Map entity -> response, nhưng car, renter, owner đang bị ignore
        var listResponse = listRentalContacts.stream()
                .map(rentalContactMapper::toResponse)
                .collect(Collectors.toList());

        for (int i = 0; i < listRentalContacts.size(); i++) {
            RentalContact rentalContact = listRentalContacts.get(i);
            RentalContactResponse response = listResponse.get(i);

            CarResponse carResponse = carService.getCar(rentalContact.getCar().toHexString());
            response.setCar(carResponse);

            UserResponse renter = userService.getUserById(rentalContact.getRenter().toHexString());
            response.setRenter(renter);

            UserResponse owner = userService.getUserById(carResponse.ownerId());
            response.setOwner(owner);
        }

        return listResponse;
    }

    @Override
    public List<RentalContactResponse> getRentalContactsByOwner() {
        var context = SecurityContextHolder.getContext();
        if (context.getAuthentication() == null || !context.getAuthentication().isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String userId = context.getAuthentication().getName();
        ObjectId objectUserId = ObjectIdUtil.toObjectId(userId);
        var user = userRepository.findById(objectUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return rentalContactRepository.findByOwner(objectUserId)
                .stream()
                .map(rentalContactMapper::toResponse)
                .peek(response -> {
                    CarResponse carResponse = carService.getCar(response.getCar().id());
                    response.setCar(carResponse);
                    UserResponse userResponse = userService.getUserById(response.getRenter().id());
                    response.setRenter(userResponse);
                    response.setOwner(userService.getUserById(carResponse.ownerId()));
                })
                .toList();
    }
}
