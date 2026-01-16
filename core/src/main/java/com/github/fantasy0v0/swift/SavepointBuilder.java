package com.github.fantasy0v0.swift;

import com.github.fantasy0v0.swift.connection.ManagedConnection;
import com.github.fantasy0v0.swift.connection.ManagedSavepoint;
import com.github.fantasy0v0.swift.connection.ManagedTransaction;
import com.github.fantasy0v0.swift.exception.SwiftException;
import com.github.fantasy0v0.swift.util.LogUtil;

import java.sql.SQLException;
import java.util.function.Supplier;

/**
 * @author fan 2026/1/16
 */
public class SavepointBuilder<T> {

  private final Context context;

  private final Runnable runnable;

  private final Supplier<T> supplier;

  SavepointBuilder(Context context, Runnable runnable, Supplier<T> supplier) {
    this.context = context;
    this.runnable = runnable;
    this.supplier = supplier;
  }

  static SavepointBuilder<Object> create(Context context, Runnable runnable) {
    return new SavepointBuilder<>(context, runnable, null);
  }

  static <T> SavepointBuilder<T> create(Context context, Supplier<T> supplier) {
    return new SavepointBuilder<>(context, null, supplier);
  }

  T execute() throws SQLException {
    StopWatch stopWatch = new StopWatch();
    LogUtil.performance().trace("savepoint begin");
    try (ManagedConnection ref = ConnectionPoolUtil.getConnection(context)) {
      boolean autoCommit = ref.unwrap().getAutoCommit();
      if (autoCommit) {
        throw new SwiftException("savepoint must be used in a transaction");
      }
      ManagedTransaction transaction = ref.getTransaction(null);
      ManagedSavepoint savepoint = transaction.createSavepoint();
      try {
        T result;
        if (null != supplier) {
          result = supplier.get();
        } else {
          runnable.run();
          result = null;
        }
        savepoint.release();
        return result;
      } catch (Exception e) {
        try {
          savepoint.rollback();
        } catch (SQLException ex) {
          LogUtil.common().error("savepoint rollback failed", ex);
        }
        throw e;
      }
    } finally {
      LogUtil.performance().debug("savepoint cost: {}", stopWatch);
    }
  }

}
