package com.github.fantasy0v0.swift.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionReference extends AutoCloseable {

  ConnectionReference reference();

  boolean isInner();

  Connection unwrap();

  @Override
  void close() throws SQLException;

}
