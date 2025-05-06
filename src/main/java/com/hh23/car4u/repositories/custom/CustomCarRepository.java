package com.hh23.car4u.repositories.custom;

import com.hh23.car4u.dtos.request.CarFilterRequest;
import com.hh23.car4u.entities.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomCarRepository {
    private final MongoTemplate mongoTemplate;

    public CustomCarRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Page<Car> search(CarFilterRequest request, Pageable pageable) {
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

//      location
        if (request.location() != null) {
            criteriaList.add(Criteria.where("location.city").regex(request.location(), "i"));
        }
//      brand
        if (request.brand() != null) {
            criteriaList.add(Criteria.where("brand").regex(request.brand(), "i"));
        }
//      fuel
        if (request.fuelType() != null) {
            criteriaList.add(Criteria.where("fuelType").is(request.fuelType()));
        }
//      fuel consumption
        if (request.fuelConsumption() != null) {
            criteriaList.add(Criteria.where("fuelConsumption").lte(request.fuelConsumption()));
        }
//      transmission
        if (request.transmissionType() != null) {
            criteriaList.add(Criteria.where("transmissionType").is(request.transmissionType()));
        }
//      car type
        if (request.type() != null) {
            criteriaList.add(Criteria.where("type").is(request.type()));
        }
//      price
        if (request.minPrice() != null && request.maxPrice() != null) {
            criteriaList.add(Criteria.where("pricePerDay").gte(request.minPrice()).lte(request.maxPrice()));
        } else if (request.minPrice() != null) {
            criteriaList.add(Criteria.where("pricePerDay").gte(request.minPrice()));
        } else if (request.maxPrice() != null) {
            criteriaList.add(Criteria.where("pricePerDay").lte(request.maxPrice()));
        }
//      seats
        if (request.minSeats() != null && request.maxSeats() != null) {
            criteriaList.add(Criteria.where("numOfSeats").gte(request.minSeats()).lte(request.maxSeats()));
        } else if (request.minSeats() != null) {
            criteriaList.add(Criteria.where("numOfSeats").gte(request.minSeats()));
        } else if (request.maxSeats() != null) {
            criteriaList.add(Criteria.where("numOfSeats").lte(request.maxSeats()));
        }
//      rating
        if (request.rating() != null) {
            criteriaList.add(Criteria.where("rating").gte(request.rating()));
        }
//      delivery
        if (request.deliveryAvailable() != null) {
            criteriaList.add(Criteria.where("deliveryAvailable").is(request.deliveryAvailable()));
        }
//      feature
        if (request.features() != null) {
            // Split the features string into a list of features
            String[] featuresArray = request.features().split(",");
            List<String> featuresList = new ArrayList<>();
            for (String feature : featuresArray) {
                featuresList.add(feature.trim());
            }
            // Add the criteria for features
            // Use Criteria.where("features").in(featuresList) to match any of the features
            criteriaList.add(Criteria.where("features").in(featuresList));
        }

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));

        }
        //      sort
        if (request.sortBy() != null) {
            switch (request.sortBy()) {
                case "priceAsc" -> query.with(Sort.by(Sort.Order.asc("pricePerDay")));
                case "priceDesc" -> query.with(Sort.by(Sort.Order.desc("pricePerDay")));
                case "ratingAsc" -> query.with(Sort.by(Sort.Order.asc("rating")));
                case "ratingDesc" -> query.with(Sort.by(Sort.Order.desc("rating")));
                default -> query.with(Sort.by(Sort.Order.desc("createdAt")));
            }
        } else {
            query.with(pageable);
        }
        long total = mongoTemplate.count(query, Car.class);
        query.with(pageable);
        List<Car> cars = mongoTemplate.find(query, Car.class);

        return new PageImpl<>(cars, pageable, total);
    }
}
