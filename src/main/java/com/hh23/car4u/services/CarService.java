package com.hh23.car4u.services;

import com.hh23.car4u.dtos.request.CarCreationRequest;
import com.hh23.car4u.dtos.response.CarResponse;

import java.util.List;

public interface CarService {
    CarResponse addCar(CarCreationRequest request, String userId);

    void removeCar(String carId);

    void updateCar(String carId, String userId);

    CarResponse getCar(String carId);

    List<CarResponse> getAllCars();
}
