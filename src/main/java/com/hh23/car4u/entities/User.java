package com.hh23.car4u.entities;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@Builder
@Document(collection = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @MongoId
    String id;
    String name;
    LocalDate dob;
    String email;
    String phone;
    String password;
    List<String> roles;
    Double rating;
    String avatar;
    Integer numOfTrips;
    boolean isActive;
    @CreatedDate
    @Setter(AccessLevel.NONE)
    Instant createdAt;
    @LastModifiedDate
    Instant lastModifiedAt;
}
