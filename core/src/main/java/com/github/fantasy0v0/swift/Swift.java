package com.github.fantasy0v0.swift;

import com.github.fantasy0v0.swift.connection.ManagedConnectionPool;
import com.github.fantasy0v0.swift.connection.impl.ManagedConnectionPoolImpl;
import com.github.fantasy0v0.swift.dialect.ANSI;
import com.github.fantasy0v0.swift.dialect.SQLDialect;
import com.github.fantasy0v0.swift.exception.SwiftException;
import com.github.fantasy0v0.swift.util.LogUtil;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Supplier;

public final class Swift {

  private static Context context;

  static {
    ConnectionPoolUtil.pool = ServiceLoader.load(ManagedConnectionPool.class)
      .findFirst()
      .orElse(new ManagedConnectionPoolImpl());
    LogUtil.common().debug("使用的ConnectionPool: {}", ConnectionPoolUtil.pool.getClass().getName());
  }

  /**
   * 慢执行阈值, 单位毫秒
   */
  private static long slowExecuteThreshold = 5000;

  /**
   * 获取设置的默认Context
   */
  public synchronized static Context getContext() {
    if (null == context) {
      throw new SwiftException("未设置默认Context");
    }
    return context;
  }

  /**
   * 设置默认Context
   */
  public synchronized static void setContext(Context context) {
    Swift.context = context;
  }

  public static Context newContext(DataSource dataSource, SQLDialect dialect,
                                   StatementConfiguration statementConfiguration) {
    if (null == dialect) {
      dialect = ANSI.Instance;
      LogUtil.common().info("将使用默认方言");
    }
    if (null == statementConfiguration) {
      statementConfiguration = new StatementConfiguration();
      LogUtil.common().info("将使用默认StatementConfiguration");
    }
    return new Context(dataSource, dialect, statementConfiguration);
  }

  public static Context newContext(DataSource dataSource, SQLDialect dialect) {
    return newContext(dataSource, dialect, null);
  }

  public static Context newContext(DataSource dataSource) {
    return newContext(dataSource, null, null);
  }

  public static SelectBuilder select(String sql, List<Object> params) {
    return getContext().select(sql.trim(), params);
  }

  public static SelectBuilder select(String sql, Object... params) {
    return getContext().select(sql, Arrays.stream(params).toList());
  }

  public static InsertBuilder insert(String sql) {
    return getContext().insert(sql);
  }

  public static UpdateBuilder update(String sql) {
    return getContext().update(sql);
  }

  public static void transaction(Integer level, Runnable runnable) {
    getContext().transaction(level, runnable);
  }

  public static void transaction(Runnable runnable) {
    getContext().transaction(null, runnable);
  }

  public static <T> T transaction(Integer level, Supplier<T> supplier) {
    return getContext().transaction(level, supplier);
  }

  public static <T> T transaction(Supplier<T> supplier) {
    return transaction(null, supplier);
  }

  public static void savepoint(Runnable runnable) {
    getContext().savepoint(runnable);
  }

  public static <T> T savepoint(Supplier<T> supplier) {
    return getContext().savepoint(supplier);
  }

  public static void setSlowExecuteThreshold(long slowExecuteThreshold) {
    LogUtil.common().debug("set slowExecuteThreshold to {}", slowExecuteThreshold);
    Swift.slowExecuteThreshold = slowExecuteThreshold;
  }

  public static long getSlowExecuteThreshold() {
    return slowExecuteThreshold;
  }
}
