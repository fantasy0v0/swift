package com.github.fantasy0v0.swift;

import com.github.fantasy0v0.swift.connection.ManagedConnection;
import com.github.fantasy0v0.swift.connection.ManagedTransaction;
import com.github.fantasy0v0.swift.util.LogUtil;

import java.sql.SQLException;
import java.util.function.Supplier;

public class TransactionBuilder<T> {

  // private static final ScopedValue<Connection> CONN = ScopedValue.newInstance();

  private final Context context;

  private final Integer level;

  private final Runnable runnable;

  private final Supplier<T> supplier;

  TransactionBuilder(Context context, Integer level, Runnable runnable, Supplier<T> supplier) {
    this.context = context;
    this.level = level;
    this.runnable = runnable;
    this.supplier = supplier;
  }

  static TransactionBuilder<Object> create(Context context, Integer level, Runnable runnable) {
    return new TransactionBuilder<>(context, level, runnable, null);
  }

  static <T> TransactionBuilder<T> create(Context context, Integer level, Supplier<T> supplier) {
    return new TransactionBuilder<>(context, level, null, supplier);
  }

  T execute() throws SQLException {
    StopWatch stopWatch = new StopWatch();
    LogUtil.performance().trace("transaction begin");
    try (ManagedConnection ref = ConnectionPoolUtil.getConnection(context)) {
      ManagedTransaction transaction = ref.getTransaction(level);
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
        try {
          transaction.rollback();
        } catch (SQLException ex) {
          LogUtil.common().error("rollback failed", ex);
        }
        throw e;
      }
    } finally {
      LogUtil.performance().debug("transaction cost: {}", stopWatch);
    }
  }

}
