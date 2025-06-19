package com.hh23.car4u.services.impl;

import com.hh23.car4u.dtos.PageResponse;
import com.hh23.car4u.dtos.request.CarCreationRequest;
import com.hh23.car4u.dtos.request.CarFilterRequest;
import com.hh23.car4u.dtos.response.CarResponse;
import com.hh23.car4u.dtos.response.UserResponse;
import com.hh23.car4u.entities.Car;
import com.hh23.car4u.entities.User;
import com.hh23.car4u.entities.enums.CarStatus;
import com.hh23.car4u.exception.AppException;
import com.hh23.car4u.exception.ErrorCode;
import com.hh23.car4u.mappers.CarMapper;
import com.hh23.car4u.mappers.UserMapper;
import com.hh23.car4u.repositories.CarRepository;
import com.hh23.car4u.repositories.UserRepository;
import com.hh23.car4u.repositories.custom.CustomCarRepository;
import com.hh23.car4u.services.CarService;
import com.hh23.car4u.utils.ObjectIdUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CarServiceImpl implements CarService {
    CarRepository carRepository;
    CustomCarRepository customCarRepository;
    CarMapper carMapper;
    UserRepository userRepository;
    UserMapper userMapper;

    private static final int PAGE_SIZE = 10;

    @Override
    public CarResponse addCar(CarCreationRequest request, String userId) {
        ObjectId objectId = ObjectIdUtil.toObjectId(userId);
        User user = userRepository.findById(objectId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Car car = carMapper.toEntity(request);
        String carId = ObjectIdUtil.toStringId(car.getId());
        car.setOwnerId(userId);
        car = carRepository.save(car);
        return carMapper.toResponse(car);
    }

    @Override
    public void removeCar(String carId) {
        ObjectId objectId = ObjectIdUtil.toObjectId(carId);
        carRepository.deleteById(objectId);
    }

    @Override
    public void updateCar(String carId, String userId) {

    }

    @Override
    public CarResponse getCar(String carId) {
        ObjectId objectId = ObjectIdUtil.toObjectId(carId);
        return carRepository.findById(objectId)
                .map(carMapper::toResponse)
                .orElseThrow(() -> new AppException(ErrorCode.CAR_NOT_FOUND));
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

    @Override
    public UserResponse getCarOwner(String carId) {
        ObjectId ObjectCarId = ObjectIdUtil.toObjectId(carId);
        Car car = carRepository.findById(ObjectCarId)
                .orElseThrow(() -> new AppException(ErrorCode.CAR_NOT_FOUND));
        Optional<User> user = userRepository.findById(ObjectIdUtil.toObjectId(car.getOwnerId()));
        if (user.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        if (user.isPresent()) {
            return userMapper.toResponse(user.get());
        }
        return null;
    }

    @Override
    public CarResponse disableCar(String carId) {
        ObjectId objectId = ObjectIdUtil.toObjectId(carId);
        Car car = carRepository.findById(objectId)
                .orElseThrow(() -> new AppException(ErrorCode.CAR_NOT_FOUND));
        var context = SecurityContextHolder.getContext();
        if (context.getAuthentication() == null || !context.getAuthentication().isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String userId = context.getAuthentication().getName();
        ObjectId objectUserId = ObjectIdUtil.toObjectId(userId);
        var user = userRepository.findById(objectUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (!car.getOwnerId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        car.setStatus(CarStatus.UNAVAILABLE);
        return carMapper.toResponse(carRepository.save(car));
    }

    @Override
    public CarResponse enableCar(String carId) {
        ObjectId objectId = ObjectIdUtil.toObjectId(carId);
        Car car = carRepository.findById(objectId)
                .orElseThrow(() -> new AppException(ErrorCode.CAR_NOT_FOUND));
        var context = SecurityContextHolder.getContext();
        if (context.getAuthentication() == null || !context.getAuthentication().isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String userId = context.getAuthentication().getName();
        ObjectId objectUserId = ObjectIdUtil.toObjectId(userId);
        var user = userRepository.findById(objectUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (!car.getOwnerId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        car.setStatus(CarStatus.AVAILABLE);
        return carMapper.toResponse(carRepository.save(car));
    }
}
