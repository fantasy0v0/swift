package com.github.fantasy0v0.swift.jdbc.connection.impl;

import com.github.fantasy0v0.swift.jdbc.connection.ConnectionReference;
import com.github.fantasy0v0.swift.jdbc.connection.ConnectionTransaction;
import com.github.fantasy0v0.swift.jdbc.util.LogUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

class DefaultConnectionTransaction implements ConnectionTransaction {

  private final ConnectionReference connectionReference;

  private final Integer level;

  private Boolean oldAutoCommit;

  Savepoint savepoint = null;

  DefaultConnectionTransaction(ConnectionReference connectionReference, Integer level) throws SQLException {
    this.connectionReference = connectionReference;
    this.level = level;
    init();
  }

  private void init() throws SQLException {
    Connection connection = connectionReference.unwrap();
    if (connection.getAutoCommit()) {
      LogUtil.common().debug("save AutoCommit: {}", oldAutoCommit);
      oldAutoCommit = true;
    }
    connection.setAutoCommit(false);
    if (null != level) {
      LogUtil.common()
        .debug("set TransactionIsolation: {}", level);
      connection.setTransactionIsolation(level);
    }
    LogUtil.common().debug("savepoint");
    savepoint = connection.setSavepoint();
  }

  private void restore() throws SQLException {
    Connection connection = connectionReference.unwrap();
    if (null != oldAutoCommit) {
      LogUtil.common().debug("restore AutoCommit: {}", oldAutoCommit);
      connection.setAutoCommit(oldAutoCommit);
    }
  }

  @Override
  public void commit() throws SQLException {
    Connection connection = connectionReference.unwrap();
    LogUtil.common().debug("release savepoint");
    connection.releaseSavepoint(savepoint);
    restore();
  }

  @Override
  public void rollback() throws SQLException {
    Connection connection = connectionReference.unwrap();
    LogUtil.common().debug("rollback by savepoint");
    connection.rollback(savepoint);
    restore();
  }
}
