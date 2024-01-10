package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.util.LogUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.text.NumberFormat;

public class TransactionBuilder {

  // private static final ScopedValue<Connection> CONN = ScopedValue.newInstance();

  private final DataSource dataSource;

  private final Integer level;

  private final Runnable runnable;

  private Boolean oldAutoCommit;

  private Integer oldLevel;

  TransactionBuilder(DataSource dataSource, Integer level, Runnable runnable) {
    this.dataSource = dataSource;
    this.level = level;
    this.runnable = runnable;
  }

  private ConnectionReference getConnection() throws SQLException {
    ConnectionReference ref = ConnectionReference.getReference(dataSource);
    try {
      Connection connection = ref.unwrap();
      if (connection.getAutoCommit()) {
        oldAutoCommit = true;
        LogUtil.common().debug("save AutoCommit: {}", oldAutoCommit);
      }
      connection.setAutoCommit(false);
      if (null != level) {
        oldLevel = connection.getTransactionIsolation();
        connection.setTransactionIsolation(level);
        LogUtil.common()
          .debug("save TransactionIsolation: {}, set TransactionIsolation: {}",
            oldLevel, level);
      }
      return ref;
    } catch (SQLException | RuntimeException e) {
      ref.close();
      throw e;
    }
  }

  private void closeConnection(ConnectionReference wrap) throws SQLException {
    Connection connection = wrap.unwrap();
    if (null != oldLevel) {
      connection.setTransactionIsolation(oldLevel);
      LogUtil.common().debug("restore TransactionIsolation: {}", oldLevel);
    }
    if (null != oldAutoCommit) {
      connection.setAutoCommit(oldAutoCommit);
      LogUtil.common().debug("restore AutoCommit: {}", oldAutoCommit);
    }
    wrap.close();
  }

  void execute() throws SQLException {
    LogUtil.performance().info("transaction begin");
    long startTime = System.nanoTime() / 1000;
    ConnectionReference ref = getConnection();
    try {
      Connection connection = ref.unwrap();
      Savepoint savepoint = null;
      if (ref.isInner()) {
        savepoint = connection.setSavepoint();
        LogUtil.common().debug("savepoint");
      }
      try {
        runnable.run();
        if (!ref.isInner()) {
          connection.commit();
          LogUtil.common().debug("commit");
        }
      } catch (Exception e) {
        if (null != savepoint) {
          connection.rollback(savepoint);
          LogUtil.common().debug("rollback by savepoint");
        } else {
          connection.rollback();
          LogUtil.common().debug("rollback");
        }
        throw e;
      }
    } finally {
      closeConnection(ref);
      long cost = System.nanoTime() / 1000 - startTime;
      NumberFormat format = NumberFormat.getNumberInstance();
      LogUtil.performance().info("transaction end, cost: {} μs", format.format(cost));
    }
  }

}
