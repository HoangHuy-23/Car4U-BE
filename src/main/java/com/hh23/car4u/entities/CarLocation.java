package com.hh23.car4u.entities;

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
public class CarLocation {
    String fullAddress;         // VD: 123 Nguyễn Văn A, P.Bến Nghé
    String no;                  // VD: 123
    String street;              // VD: Nguyễn Văn A
    String ward;               // VD: Phường Bến Nghé
    String district;            // VD: Quận 1
    String city;                // VD: Hồ Chí Minh
    List<Double> coordinates;   // [lng, lat]
}
