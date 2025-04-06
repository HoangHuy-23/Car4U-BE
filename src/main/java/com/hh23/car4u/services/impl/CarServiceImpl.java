package com.hh23.car4u.services.impl;

import com.hh23.car4u.dtos.PageResponse;
import com.hh23.car4u.dtos.request.CarCreationRequest;
import com.hh23.car4u.dtos.request.CarFilterRequest;
import com.hh23.car4u.dtos.response.CarResponse;
import com.hh23.car4u.entities.Car;
import com.hh23.car4u.entities.User;
import com.hh23.car4u.exception.AppException;
import com.hh23.car4u.exception.ErrorCode;
import com.hh23.car4u.mappers.CarMapper;
import com.hh23.car4u.repositories.CarRepository;
import com.hh23.car4u.repositories.UserRepository;
import com.hh23.car4u.repositories.custom.CustomCarRepository;
import com.hh23.car4u.services.CarService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CarServiceImpl implements CarService {
    CarRepository carRepository;
    CustomCarRepository customCarRepository;
    CarMapper carMapper;

    UserRepository userRepository;

    private static final int PAGE_SIZE = 10;

    @Override
    public CarResponse addCar(CarCreationRequest request, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Car car = carMapper.toEntity(request);
        car.setOwnerId(user.getId());
        car = carRepository.save(car);
        return carMapper.toResponse(car);
    }

    @Override
    public void removeCar(String carId) {
        carRepository.deleteById(carId);
    }

    @Override
    public void updateCar(String carId, String userId) {

    }

    @Override
    public CarResponse getCar(String carId) {
        return carRepository.findById(carId)
                .map(carMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Car not found"));
    }

    @Override
    public List<CarResponse> getAllCars() {
        return carRepository.findAll()
                .stream()
                .map(carMapper::toResponse)
                .toList();
    }

    @Override
    public PageResponse<CarResponse> filterCar(Integer pageNo, CarFilterRequest request) {
        Pageable pageable = PageRequest.of(pageNo - 1, PAGE_SIZE);
        var cars = customCarRepository.search(request, pageable);
        var carResponses = cars.getContent()
                .stream()
                .map(carMapper::toResponse)
                .toList();
        return PageResponse.<CarResponse>builder()
                .currentPage(pageNo)
                .pageSize(PAGE_SIZE)
                .totalElements(cars.getTotalElements())
                .totalPages(cars.getTotalPages())
                .data(carResponses)
                .build();
    }
}
