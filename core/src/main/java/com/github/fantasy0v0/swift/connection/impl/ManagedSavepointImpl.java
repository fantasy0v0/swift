package com.github.fantasy0v0.swift.connection.impl;

import com.github.fantasy0v0.swift.connection.ManagedConnection;
import com.github.fantasy0v0.swift.connection.ManagedSavepoint;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

/**
 * @author fan 2026/1/16
 */
class ManagedSavepointImpl implements ManagedSavepoint {

  private final Connection connection;

  private final Savepoint savepoint;

  ManagedSavepointImpl(ManagedConnection connection) throws SQLException {
    this.connection = connection.unwrap();
    savepoint = this.connection.setSavepoint();
  }

  @Override
  public void release() throws SQLException {
    connection.releaseSavepoint(savepoint);
  }

  @Override
  public void rollback() throws SQLException {
    connection.rollback(savepoint);
  }
}
