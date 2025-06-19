package com.hh23.car4u.mappers;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Component
public class ObjectIdMapper {
    public String objectIdToString(ObjectId objectId) {
        return objectId != null ? objectId.toHexString() : null;
    }

    public ObjectId stringToObjectId(String id) {
        return (id != null && ObjectId.isValid(id)) ? new ObjectId(id) : null;
    }
}
