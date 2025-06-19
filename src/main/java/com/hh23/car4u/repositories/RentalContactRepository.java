package com.hh23.car4u.repositories;

import com.hh23.car4u.entities.RentalContact;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalContactRepository extends MongoRepository<RentalContact, ObjectId> {
    List<RentalContact> findByCar(ObjectId carId);

    List<RentalContact> findByRenter(ObjectId renter);

    List<RentalContact> findByOwner(ObjectId owner);
}
