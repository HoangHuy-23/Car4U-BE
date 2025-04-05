package com.hh23.car4u.repositories;

import com.hh23.car4u.entities.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
    Optional<RefreshToken> findByUserId(String userId);
    void deleteByUserId(String userId);
}
