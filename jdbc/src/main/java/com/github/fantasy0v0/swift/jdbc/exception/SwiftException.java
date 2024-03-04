package com.github.fantasy0v0.swift.jdbc.exception;

public class SwiftException extends RuntimeException {

  public SwiftException(String message, Throwable throwable) {
    super(message, throwable);
  }

  public SwiftException(String message) {
    super(message);
  }

  public SwiftException(Throwable throwable) {
    super(throwable);
  }

}
