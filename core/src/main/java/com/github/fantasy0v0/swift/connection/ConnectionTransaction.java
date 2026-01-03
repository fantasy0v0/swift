package com.github.fantasy0v0.swift.connection;

import java.sql.SQLException;

public interface ConnectionTransaction {

  void commit() throws SQLException;

  void rollback() throws SQLException;

}
