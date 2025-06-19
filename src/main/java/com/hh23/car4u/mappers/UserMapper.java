package com.hh23.car4u.mappers;

import com.hh23.car4u.dtos.request.LoginSocialRequest;
import com.hh23.car4u.dtos.request.UserCreationRequest;
import com.hh23.car4u.dtos.response.UserResponse;
import com.hh23.car4u.entities.User;
import com.hh23.car4u.utils.ObjectIdUtil;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = ObjectIdMapper.class)
public interface UserMapper {
    User toEntity(UserCreationRequest userCreationRequest);
    User toEntity(LoginSocialRequest loginSocialRequest);
    @Mapping(source = "id", target = "id", qualifiedByName = "objectIdToString")
    UserResponse toResponse(User user);

    @Named("objectIdToString")
    static String objectIdToString(ObjectId objectId) {
        return objectId != null ? objectId.toHexString() : null;
    }
}
