package com.github.fantasy0v0.swift.connection.impl;

import com.github.fantasy0v0.swift.connection.ConnectionReference;
import com.github.fantasy0v0.swift.connection.ConnectionTransaction;
import com.github.fantasy0v0.swift.util.LogUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

class DefaultConnectionTransaction implements ConnectionTransaction {

  private final ConnectionReference connectionReference;

  private final Integer level;

  private Boolean oldAutoCommit;

  private int oldLevel;

  Savepoint savepoint = null;

  DefaultConnectionTransaction(ConnectionReference connectionReference, Integer level) throws SQLException {
    this.connectionReference = connectionReference;
    this.level = level;
    init();
  }

  private void init() throws SQLException {
    Connection connection = connectionReference.unwrap();
    if (connection.getAutoCommit()) {
      oldAutoCommit = true;
      LogUtil.common().debug("save AutoCommit: {}", oldAutoCommit);
    }
    connection.setAutoCommit(false);
    oldLevel = connection.getTransactionIsolation();
    if (null != level && level != oldLevel) {
      LogUtil.common().debug("set TransactionIsolation: {}", level);
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
    if (null != level && level != oldLevel) {
      LogUtil.common().debug("restore TransactionIsolation: {}", oldLevel);
      connection.setTransactionIsolation(oldLevel);
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
