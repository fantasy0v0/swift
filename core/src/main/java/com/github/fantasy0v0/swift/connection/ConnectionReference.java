package com.github.fantasy0v0.swift.connection;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionReference extends AutoCloseable {

  ConnectionReference reference();

  ConnectionTransaction getTransaction(Integer level) throws SQLException;

  Connection unwrap() throws SQLException;

  @Override
  void close() throws SQLException;

}
