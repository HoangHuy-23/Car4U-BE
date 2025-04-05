package com.hh23.car4u.repositories;

import com.hh23.car4u.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByGoogleAccountId(String googleAccountId);
    Optional<User> findByFacebookAccountId(String facebookAccountId);
    boolean existsByEmail(String email);
}
