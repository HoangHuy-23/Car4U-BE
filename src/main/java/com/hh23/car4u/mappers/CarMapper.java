package com.hh23.car4u.mappers;

import com.hh23.car4u.dtos.request.CarCreationRequest;
import com.hh23.car4u.dtos.response.CarResponse;
import com.hh23.car4u.entities.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CarMapper {
    @Mapping(target = "numOfTrips", constant = "0")
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "status", constant = "UNAVAILABLE")
    @Mapping(target = "rating", constant = "0.0")
    @Mapping(target = "numOfRatings", constant = "0")
    @Mapping(target = "isVerified", constant = "false")
    Car toEntity(CarCreationRequest request);
    CarResponse toResponse(Car car);
}
