package com.hh23.car4u.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hh23.car4u.entities.enums.UserAddressType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Setter
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAddress {
    String id;                  // Unique identifier for the address
    String reminder;              // VD: Nhà, Công ty, Bố mẹ
    @JsonProperty("isDefault")
    boolean isDefault;          // Địa chỉ mặc định cho người dùng
    String no;                  // VD: 123
    String street;              // VD: Nguyễn Văn A
    String ward;                // VD: Phường Bến Nghé
    String district;            // VD: Quận 1
    String city;                // VD: Hồ Chí Minh
    List<Double> coordinates;   // [lng, lat]
    UserAddressType type; // Loại địa chỉ (Nhà, Công ty, Bố mẹ, v.v.)
}
