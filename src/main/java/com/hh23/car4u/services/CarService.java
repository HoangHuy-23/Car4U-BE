package com.hh23.car4u.services;

import com.hh23.car4u.dtos.PageResponse;
import com.hh23.car4u.dtos.request.CarCreationRequest;
import com.hh23.car4u.dtos.request.CarFilterRequest;
import com.hh23.car4u.dtos.response.CarResponse;
import com.hh23.car4u.dtos.response.UserResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CarService {
    CarResponse addCar(CarCreationRequest request, String userId);

    void removeCar(String carId);

    void updateCar(String carId, String userId);

    CarResponse getCar(String carId);

    List<CarResponse> getAllCars();

    PageResponse<CarResponse> filterCar(Integer pageNo, CarFilterRequest request);

    UserResponse getCarOwner(String carId);

    CarResponse disableCar(String carId);
    CarResponse enableCar(String carId);
}
