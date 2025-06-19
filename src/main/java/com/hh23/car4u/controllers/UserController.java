package com.hh23.car4u.controllers;

import com.hh23.car4u.dtos.ApiResponse;
import com.hh23.car4u.dtos.request.DriverLicenseRequest;
import com.hh23.car4u.dtos.request.UserAddressRequest;
import com.hh23.car4u.dtos.request.UserInfoUpdate;
import com.hh23.car4u.dtos.response.CarResponse;
import com.hh23.car4u.dtos.response.UserResponse;
import com.hh23.car4u.services.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUserById(@PathVariable String userId) {
        log.info("Get user with id: {}", userId);
        UserResponse user = userService.getUserById(userId);
        return ApiResponse.<UserResponse>builder()
                .message("Get user successfully")
                .data(user)
                .build();
    }

    @GetMapping("/my-cars")
    public ApiResponse<List<CarResponse>> getMyCars() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Get cars for user with id: {}", userId);
        List<CarResponse> cars = userService.getMyCars();
        return ApiResponse.<List<CarResponse>>builder()
                .message("Get user's cars successfully")
                .data(cars)
                .build();
    }

    @GetMapping("/my-favorites")
    public ApiResponse<List<CarResponse>> getMyFavorites() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Get favorite cars for user with id: {}", userId);
        List<CarResponse> favorites = userService.getMyFavorites();
        return ApiResponse.<List<CarResponse>>builder()
                .message("Get user's favorite cars successfully")
                .data(favorites)
                .build();
    }

    @PostMapping("/like-car/{carId}")
    public ApiResponse<Void> likeCar(@PathVariable String carId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("User with id: {} likes car with id: {}", userId, carId);
        userService.likeCar(carId);
        return ApiResponse.<Void>builder()
                .message("Car liked successfully")
                .build();
    }

    @DeleteMapping("/unlike-car/{carId}")
    public ApiResponse<Void> unlikeCar(@PathVariable String carId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("User with id: {} unlikes car with id: {}", userId, carId);
        userService.unlikeCar(carId);
        return ApiResponse.<Void>builder()
                .message("Car unliked successfully")
                .build();
    }

    @PostMapping("/add-address")
    public ApiResponse<UserResponse> addAddress(@RequestBody UserAddressRequest address) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("User with id: {} adds address: {}", userId, address);
        var data = userService.addAddress(address);
        return ApiResponse.<UserResponse>builder()
                .message("Address added successfully")
                .data(data)
                .build();
    }

    @PutMapping("/update-address/{addressId}")
    public ApiResponse<UserResponse> updateAddress(@RequestBody UserAddressRequest address, @PathVariable String addressId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("User with id: {} updates address with id: {} to {}", userId, addressId, address);
        var data = userService.updateAddress(address, addressId);
        return ApiResponse.<UserResponse>builder()
                .message("Address updated successfully")
                .data(data)
                .build();
    }

    @DeleteMapping("/delete-address/{addressId}")
    public ApiResponse<UserResponse> deleteAddress(@PathVariable String addressId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("User with id: {} deletes address with id: {}", userId, addressId);
        var data = userService.deleteAddress(addressId);
        return ApiResponse.<UserResponse>builder()
                .message("Address deleted successfully")
                .data(data)
                .build();
    }

    @PostMapping("/make-default-address/{addressId}")
    public ApiResponse<UserResponse> makeAddressDefault(@PathVariable String addressId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("User with id: {} makes address with id: {} default", userId, addressId);
        var data = userService.makeAddressDefault(addressId);
        return ApiResponse.<UserResponse>builder()
                .message("Address made default successfully")
                .data(data)
                .build();
    }

    @PutMapping("/update-driver-license")
    public ApiResponse<UserResponse> updateDriverLicense(@ModelAttribute DriverLicenseRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("User with id: {} updates driver license", userId);
        try {
            var data = userService.updateDriverLicense(request);
            return ApiResponse.<UserResponse>builder()
                    .message("Driver license updated successfully")
                    .data(data)
                    .build();
        } catch (IOException e) {
            log.error("Error updating driver license for user with id: {}", userId, e);
            return ApiResponse.<UserResponse>builder()
                    .message("Failed to update driver license")
                    .build();
        }
    }

    @PutMapping("/update-phone")
    public ApiResponse<UserResponse> updatePhone(@RequestParam String phone) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("User with id: {} updates phone to {}", userId, phone);
        var data = userService.updatePhone(phone);
        return ApiResponse.<UserResponse>builder()
                .message("Phone updated successfully")
                .data(data)
                .build();
    }

    @PutMapping("/update-info")
    public ApiResponse<UserResponse> updateInfo(@RequestBody UserInfoUpdate request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("User with id: {} updates info", userId);
        var data = userService.updateInfo(request);
        return ApiResponse.<UserResponse>builder()
                .message("User info updated successfully")
                .data(data)
                .build();
    }
}
