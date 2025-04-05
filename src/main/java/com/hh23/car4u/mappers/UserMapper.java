package com.hh23.car4u.mappers;

import com.hh23.car4u.dtos.request.LoginSocialRequest;
import com.hh23.car4u.dtos.request.UserCreationRequest;
import com.hh23.car4u.dtos.response.UserResponse;
import com.hh23.car4u.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserCreationRequest userCreationRequest);
    User toEntity(LoginSocialRequest loginSocialRequest);
    UserResponse toResponse(User user);
}
