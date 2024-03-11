package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.util.LogUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.text.NumberFormat;
import java.util.function.Supplier;

public class TransactionBuilder<T> {

  // private static final ScopedValue<Connection> CONN = ScopedValue.newInstance();

  private final DataSource dataSource;

  private final Integer level;

  private final Runnable runnable;

  private final Supplier<T> supplier;

  private Boolean oldAutoCommit;

  private Integer oldLevel;

  TransactionBuilder(DataSource dataSource, Integer level, Runnable runnable, Supplier<T> supplier) {
    this.dataSource = dataSource;
    this.level = level;
    this.runnable = runnable;
    this.supplier = supplier;
  }

  static TransactionBuilder<Object> create(DataSource dataSource, Integer level, Runnable runnable) {
    return new TransactionBuilder<>(dataSource, level, runnable, null);
  }

  static <T> TransactionBuilder<T> create(DataSource dataSource, Integer level, Supplier<T> supplier) {
    return new TransactionBuilder<>(dataSource, level, null, supplier);
  }

  private ConnectionReference getConnection() throws SQLException {
    ConnectionReference ref = ConnectionReference.getReference(dataSource);
    try {
      Connection connection = ref.unwrap();
      if (connection.getAutoCommit()) {
        LogUtil.common().debug("save AutoCommit: {}", oldAutoCommit);
        oldAutoCommit = true;
      }
      connection.setAutoCommit(false);
      if (null != level) {
        oldLevel = connection.getTransactionIsolation();
        LogUtil.common()
          .debug(
            "save TransactionIsolation: {}, set TransactionIsolation: {}",
            oldLevel, level
          );
        connection.setTransactionIsolation(level);
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
      LogUtil.common().debug("restore TransactionIsolation: {}", oldLevel);
      connection.setTransactionIsolation(oldLevel);
    }
    if (null != oldAutoCommit) {
      LogUtil.common().debug("restore AutoCommit: {}", oldAutoCommit);
      connection.setAutoCommit(oldAutoCommit);
    }
    wrap.close();
  }

  T execute() throws SQLException {
    LogUtil.performance().info("transaction begin");
    long startTime = System.nanoTime() / 1000;
    ConnectionReference ref = getConnection();
    try {
      Connection connection = ref.unwrap();
      Savepoint savepoint = null;
      if (ref.isInner()) {
        LogUtil.common().debug("savepoint");
        savepoint = connection.setSavepoint();
      }
      try {
        T result;
        if (null != supplier) {
          result = supplier.get();
        } else {
          runnable.run();
          result = null;
        }
        if (null != savepoint) {
          LogUtil.common().debug("release savepoint");
          connection.releaseSavepoint(savepoint);
        }
        if (!ref.isInner()) {
          LogUtil.common().debug("commit");
          connection.commit();
        }
        return result;
      } catch (Exception e) {
        if (null != savepoint) {
          LogUtil.common().debug("rollback by savepoint");
          connection.rollback(savepoint);
        } else {
          LogUtil.common().debug("rollback");
          connection.rollback();
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
