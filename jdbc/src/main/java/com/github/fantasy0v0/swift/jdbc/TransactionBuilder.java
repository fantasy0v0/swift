package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.util.LogUtil;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.function.Supplier;

public class TransactionBuilder<T> {

  // private static final ScopedValue<Connection> CONN = ScopedValue.newInstance();

  private final DataSource dataSource;

  private final Integer level;

  private final Runnable runnable;

  private final Supplier<T> supplier;

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

  T execute() throws SQLException {
    LogUtil.performance().info("transaction begin");
    long startTime = System.nanoTime() / 1000;
    ConnectionReference ref = ConnectionPoolUtil.getReference(dataSource);
    ConnectionTransaction transaction = ref.getTransaction(level);
    try {
      try {
        T result;
        if (null != supplier) {
          result = supplier.get();
        } else {
          runnable.run();
          result = null;
        }
        transaction.commit();
        return result;
      } catch (Exception e) {
        transaction.rollback();
        throw e;
      }
    } finally {
      ConnectionPoolUtil.closeReference(ref, dataSource);
      long cost = System.nanoTime() / 1000 - startTime;
      NumberFormat format = NumberFormat.getNumberInstance();
      LogUtil.performance().info("transaction end, cost: {} μs", format.format(cost));
    }
  }

}
