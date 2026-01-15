package com.github.fantasy0v0.swift.connection;

import java.sql.SQLException;

public interface ManagedTransaction {

  void commit() throws SQLException;

  void rollback() throws SQLException;

}
