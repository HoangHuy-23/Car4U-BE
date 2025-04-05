package com.hh23.car4u.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;

@Setter
@Getter
@Builder
@Document(collection = "refresh_tokens")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshToken {
    @MongoId
    String id;
    String userId;
    String token;
    Instant expiresAt;
}
