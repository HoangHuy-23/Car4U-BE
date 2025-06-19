package com.hh23.car4u.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION("Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_NOT_FOUND("User not found", HttpStatus.NOT_FOUND),
    USER_EXISTED("Username already exists", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED("Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED("You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_CREDENTIALS("Invalid credentials", HttpStatus.UNAUTHORIZED),
    INVALID_ID_FORMAT("Invalid ID format", HttpStatus.BAD_REQUEST),
//    car
    CAR_NOT_FOUND("Car not found", HttpStatus.NOT_FOUND),
//  user
    ADDRESS_NOT_FOUND("Address not found", HttpStatus.NOT_FOUND),
    INVALID_PHONE_NUMBER("Invalid phone number format", HttpStatus.BAD_REQUEST),
    PHONE_ALREADY_EXISTS("Phone number already exists", HttpStatus.BAD_REQUEST),
    RENTAL_CONTACT_NOT_FOUND("Rental contact not found", HttpStatus.NOT_FOUND)
    ;

    ErrorCode(String message, HttpStatus statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    private final String message;
    private final HttpStatus statusCode;
}
