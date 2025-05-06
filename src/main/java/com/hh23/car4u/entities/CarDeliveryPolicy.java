package com.hh23.car4u.entities;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CarDeliveryPolicy {
    // Giao – nhận xe tận nơi
    Boolean allowDelivery;                  // Có cho phép giao xe tận nơi không?
    Double freeDeliveryRadiusKm;           // Bán kính giao miễn phí (km)
    Double maxDeliveryDistanceKm;          // Tối đa bán kính giao xe (km)
    Double deliveryFeePerKm;               // Phí/km tính 2 chiều (VD: 30.000 đ/km)

}
