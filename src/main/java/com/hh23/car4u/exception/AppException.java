package com.hh23.car4u.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AppException extends RuntimeException {
  public AppException(ErrorCode errorCode) {
    super(errorCode.getMessage(), null, false, false);
    this.errorCode = errorCode;
  }
  private ErrorCode errorCode;

  @Override
  public synchronized Throwable fillInStackTrace() {
    return this;
  }
}
