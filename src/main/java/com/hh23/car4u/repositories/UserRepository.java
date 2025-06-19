package com.hh23.car4u.repositories;

import com.hh23.car4u.entities.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {
    Optional<User> findByEmail(String email);
    Optional<User> findByGoogleAccountId(String googleAccountId);
    Optional<User> findByFacebookAccountId(String facebookAccountId);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
