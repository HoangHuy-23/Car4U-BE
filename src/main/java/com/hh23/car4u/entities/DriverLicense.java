package com.hh23.car4u.entities;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Setter
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DriverLicense {
    String licenseNumber; // Số giấy phép lái xe
    String name; // Tên người lái xe
    LocalDate dob;
    String image; // Đường dẫn đến ảnh giấy phép lái xe
    Boolean isVerified; // Đã xác thực chưa
}
