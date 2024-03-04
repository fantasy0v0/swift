package com.github.fantasy0v0.swift.jdbc.exception;

import java.sql.SQLException;

public class SwiftSQLException extends RuntimeException {

  /**
   * @serial
   */
  private String sqlState;

  /**
   * @serial
   */
  private int vendorCode;

  public SwiftSQLException(SQLException cause) {
    super(cause);
  }

  public SwiftSQLException(String message, SQLException cause) {
    super(message, cause);
    this.sqlState = cause.getSQLState();
    this.vendorCode = cause.getErrorCode();
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
    return vendorCode;
  }

}
