package com.github.fantasy0v0.swift.connection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 该类非线程安全, 请勿跨线程使用
 */
public interface ManagedConnection extends AutoCloseable {

  ManagedConnection retain();

  ManagedTransaction getTransaction(Integer level) throws SQLException;

  Connection unwrap() throws SQLException;

  @Override
  void close() throws SQLException;

}
