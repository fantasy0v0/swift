package com.github.fantasy0v0.swift.exception;

import java.sql.SQLException;

public class SwiftSQLException extends SwiftException {

  /**
   * @serial
   */
  private String sqlState;

  /**
   * @serial
   */
  private int errorCode;

  public SwiftSQLException(SQLException cause) {
    super(cause);
    errorCode = cause.getErrorCode();
    sqlState = cause.getSQLState();
  }

  /**
   * Retrieves the SQLState for this {@code SQLException} object.
   *
   * @return the SQLState value
   */
  public String getSQLState() {
    return sqlState;
  }

  /**
   * Retrieves the vendor-specific exception code
   * for this {@code SQLException} object.
   *
   * @return the vendor's error code
   */
  public int getErrorCode() {
    return errorCode;
  }

}
