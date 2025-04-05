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
    ;

    ErrorCode(String message, HttpStatus statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    private final String message;
    private final HttpStatus statusCode;
}
