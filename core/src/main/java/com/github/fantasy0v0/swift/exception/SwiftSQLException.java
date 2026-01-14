package com.github.fantasy0v0.swift.exception;

import java.sql.SQLException;
import java.util.Iterator;

/**
 * 将SQLException包装成SwiftException
 */
public class SwiftSQLException extends SwiftException {

  /**
   * An exception that provides information on a database access error or other errors.
   */
  private final SQLException cause;

  public SwiftSQLException(SQLException cause) {
    super(cause.getMessage(), cause);
    this.cause = cause;
  }

  public SQLException getSQLException() {
    return cause;
  }

  /**
   * Retrieves the SQLState for this {@code SQLException} object.
   *
   * @return the SQLState value
   */
  public String getSQLState() {
    return cause.getSQLState();
  }

  /**
   * Retrieves the vendor-specific exception code
   * for this {@code SQLException} object.
   *
   * @return the vendor's error code
   */
  public int getErrorCode() {
    return cause.getErrorCode();
  }

  /**
   * Retrieves the exception chained to this
   * {@code SQLException} object by setNextException(SQLException ex).
   *
   * @return the next {@code SQLException} object in the chain;
   * {@code null} if there are none
   */
  public SQLException getNextException() {
    return cause.getNextException();
  }

  /**
   * Returns an iterator over the chained SQLExceptions.  The iterator will
   * be used to iterate over each SQLException and its underlying cause
   * (if any).
   *
   * @return an iterator over the chained SQLExceptions and causes in the proper
   * order
   * @since 1.6
   */
  public Iterator<Throwable> iterator() {
    return cause.iterator();
  }
}
