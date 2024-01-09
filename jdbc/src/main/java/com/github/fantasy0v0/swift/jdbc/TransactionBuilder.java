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

  /**
   * 在其他事物里面
   */
  private boolean inside;

  TransactionBuilder(DataSource dataSource, Integer level, Runnable runnable) {
    this.dataSource = dataSource;
    this.level = level;
    this.runnable = runnable;
  }

  private Connection getConnection() throws SQLException {
    // TODO 判断是否在其他事物中
    Connection connection = Utils.getConnection(dataSource);
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
    return connection;
  }

  private void closeConnection(Connection connection) throws SQLException {
    if (null != oldLevel) {
      connection.setTransactionIsolation(oldLevel);
      LogUtil.common().debug("restore TransactionIsolation: {}", oldLevel);
    }
    if (null != oldAutoCommit) {
      connection.setAutoCommit(oldAutoCommit);
      LogUtil.common().debug("restore AutoCommit: {}", oldAutoCommit);
    }
    connection.close();
  }

  void execute() throws SQLException {
    LogUtil.performance().info("transaction begin");
    long startTime = System.nanoTime() / 1000;
    Connection connection = getConnection();
    try {
      Savepoint savepoint = null;
      if (inside) {
        savepoint = connection.setSavepoint();
        LogUtil.common().debug("savepoint");
      }
      try {
        runnable.run();
        if (!inside) {
          connection.commit();
          LogUtil.common().debug("commit");
        }
      } catch (Exception e) {
        if (null != savepoint) {
          connection.rollback(savepoint);
        } else {
          connection.rollback();
        }
        LogUtil.common().debug("rollback");
        throw e;
      }
    } finally {
      closeConnection(connection);
      long cost = System.nanoTime() / 1000 - startTime;
      NumberFormat format = NumberFormat.getNumberInstance();
      LogUtil.performance().info("transaction end, cost: {} μs", format.format(cost));
    }
  }

}
