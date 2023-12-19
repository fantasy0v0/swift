package com.github.fantasy0v0.swift.jdbc.exception;

public class SwiftJdbcException extends RuntimeException {

  public SwiftJdbcException(String message) {
    super(message);
  }

  public SwiftJdbcException(String message, Throwable cause) {
    super(message, cause);
  }
}
