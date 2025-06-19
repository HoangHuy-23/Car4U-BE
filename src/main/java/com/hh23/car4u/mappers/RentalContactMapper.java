package com.hh23.car4u.mappers;

import com.hh23.car4u.dtos.request.RentalContactRequest;
import com.hh23.car4u.dtos.response.RentalContactResponse;
import com.hh23.car4u.entities.RentalContact;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface RentalContactMapper {

    RentalContact toEntity(RentalContactRequest request);
    @Mapping(target = "id", source = "id", qualifiedByName = "objectIdToString")
    @Mapping(target = "car", ignore = true)     // ánh xạ tay
    @Mapping(target = "renter", ignore = true)  // ánh xạ tay
    @Mapping(target = "owner", ignore = true)   // ánh xạ tay
    RentalContactResponse toResponse(RentalContact entity);

    @Named("objectIdToString")
    static String objectIdToString(ObjectId objectId) {
        return objectId != null ? objectId.toHexString() : null;
    }
}
