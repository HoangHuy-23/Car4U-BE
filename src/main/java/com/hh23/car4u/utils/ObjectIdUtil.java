package com.hh23.car4u.utils;
import com.hh23.car4u.exception.AppException;
import com.hh23.car4u.exception.ErrorCode;
import org.bson.types.ObjectId;


public class ObjectIdUtil {
    /**
     * Convert String ID sang ObjectId hợp lệ. Nếu sai định dạng sẽ ném AppException.
     *
     * @param id chuỗi id từ request
     * @return ObjectId đã convert
     */
    public static ObjectId toObjectId(String id) {
        if (id == null || !ObjectId.isValid(id)) {
            throw new AppException(ErrorCode.INVALID_ID_FORMAT);
        }
        return new ObjectId(id);
    }

    /**
     * Convert ObjectId sang String ID.
     *
     * @param objectId ObjectId cần convert
     * @return chuỗi id đã convert
     */
    public static String toStringId(ObjectId objectId) {
        if (objectId == null) {
            return null;
        }
        return objectId.toHexString();
    }
}
