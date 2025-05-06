package com.hh23.car4u.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "collaterals")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Collateral {
    @MongoId
    String id;
    // Gắn với đơn thuê xe
    String bookingId;
    // Người thuê
    String renterId;
    // Loại tài sản thế chấp
    String type; // Ví dụ: "Tiền mặt", "Xe máy", v.v.
    // Mô tả cụ thể hơn nếu cần
    String description;
    // Hình ảnh / chứng từ
    List<String> attachmentUrls;
    // Thời điểm nộp / xác nhận
    Instant submittedAt;
    // Trạng thái: đang giữ, đã trả lại, tranh chấp...
    String status; // "held", "returned", "disputed"
}
