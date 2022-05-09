package com.scb.rider.joballocation.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class JobAllocationException extends RuntimeException {
  private final String errorCode;

  private final String errorMessage;

}
