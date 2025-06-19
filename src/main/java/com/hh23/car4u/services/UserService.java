package com.hh23.car4u.services;

import com.hh23.car4u.dtos.request.DriverLicenseRequest;
import com.hh23.car4u.dtos.request.UserAddressRequest;
import com.hh23.car4u.dtos.request.UserInfoUpdate;
import com.hh23.car4u.dtos.response.CarResponse;
import com.hh23.car4u.dtos.response.UserResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface UserService {
    UserResponse getUserById(String userId);
    UserResponse getUserByEmail(String email);
    UserResponse getUserByPhone(String phone);
    List<CarResponse> getMyCars();
    List<CarResponse> getMyFavorites();
    UserResponse likeCar(String carId);
    UserResponse unlikeCar(String carId);
    UserResponse addAddress(UserAddressRequest request);
    UserResponse updateAddress(UserAddressRequest request, String addressId);
    UserResponse deleteAddress(String addressId);
    UserResponse makeAddressDefault(String addressId);
    UserResponse updateDriverLicense(DriverLicenseRequest request) throws IOException;
    UserResponse updatePhone(String phone);
    UserResponse updateInfo(UserInfoUpdate request);
}
