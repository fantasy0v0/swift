package com.github.fantasy0v0.swift.connection.impl;

import com.github.fantasy0v0.swift.connection.ManagedConnection;
import com.github.fantasy0v0.swift.connection.ManagedSavepoint;
import com.github.fantasy0v0.swift.connection.ManagedTransaction;
import com.github.fantasy0v0.swift.util.LogUtil;

import java.sql.Connection;
import java.sql.SQLException;

class ManagedTransactionImpl implements ManagedTransaction {

  private final ManagedConnection managedConnection;

  private final Integer level;

  private Boolean oldAutoCommit;

  private int oldLevel;

  ManagedTransactionImpl(ManagedConnection managedConnection, Integer level) throws SQLException {
    this.managedConnection = managedConnection;
    this.level = level;
    init();
  }

  private void init() throws SQLException {
    Connection connection = managedConnection.unwrap();
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
  }

  private void restore() throws SQLException {
    Connection connection = managedConnection.unwrap();
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
    if (Boolean.TRUE.equals(oldAutoCommit)) {
      Connection connection = managedConnection.unwrap();
      connection.commit();
      LogUtil.common().debug("connection commit by transaction");
      restore();
    } else {
      LogUtil.common().debug("skip connection commit when autoCommit is false");
    }
  }

  @Override
  public void rollback() throws SQLException {
    if (Boolean.TRUE.equals(oldAutoCommit)) {
      Connection connection = managedConnection.unwrap();
      connection.rollback();
      LogUtil.common().debug("connection rollback by transaction");
      restore();
    } else {
      LogUtil.common().debug("skip connection rollback when autoCommit is false");
    }
  }

  @Override
  public ManagedSavepoint createSavepoint() throws SQLException {
    return new ManagedSavepointImpl(managedConnection);
  }
}
