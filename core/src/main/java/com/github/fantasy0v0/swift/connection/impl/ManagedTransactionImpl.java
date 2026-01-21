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

  private Integer oldLevel;

  ManagedTransactionImpl(ManagedConnection managedConnection, Integer level) throws SQLException {
    this.managedConnection = managedConnection;
    this.level = level;
    init();
  }

  private void init() throws SQLException {
    Connection connection = managedConnection.unwrap();
    if (connection.getAutoCommit()) {
      oldAutoCommit = true;
      LogUtil.common().debug("set AutoCommit to false");
      connection.setAutoCommit(false);
    } else {
      LogUtil.common().debug("inherit the existing transaction");
    }

    // 虽然设置了autoCommit为false, 但此时还有修改TransactionIsolation的机会, 这里就交给用户自己决定
    if (null != level) {
      oldLevel = connection.getTransactionIsolation();
      LogUtil.common().debug("set TransactionIsolation: {}", level);
      connection.setTransactionIsolation(level);
    }
  }

  private void restore() throws SQLException {
    Connection connection = managedConnection.unwrap();
    if (null != oldAutoCommit && oldAutoCommit != connection.getAutoCommit()) {
      LogUtil.common().debug("restore AutoCommit: {}", oldAutoCommit);
      connection.setAutoCommit(oldAutoCommit);
    }
    if (null != level && !level.equals(oldLevel)) {
      LogUtil.common().debug("restore TransactionIsolation: {}", oldLevel);
      connection.setTransactionIsolation(oldLevel);
    }
  }

  @Override
  public void commit() throws SQLException {
    if (Boolean.TRUE.equals(oldAutoCommit)) {
      Connection connection = managedConnection.unwrap();
      try {
        connection.commit();
        LogUtil.common().debug("connection commit by transaction");
      } finally {
        restore();
      }
    } else {
      LogUtil.common().debug("skip connection commit because transaction is inherited");
    }
  }

  @Override
  public void rollback() throws SQLException {
    if (Boolean.TRUE.equals(oldAutoCommit)) {
      Connection connection = managedConnection.unwrap();
      try {
        connection.rollback();
        LogUtil.common().debug("connection rollback by transaction");
      } finally {
        restore();
      }
    } else {
      LogUtil.common().debug("skip connection rollback because transaction is inherited");
    }
  }

  @Override
  public ManagedSavepoint createSavepoint() throws SQLException {
    return new ManagedSavepointImpl(managedConnection);
  }
}
