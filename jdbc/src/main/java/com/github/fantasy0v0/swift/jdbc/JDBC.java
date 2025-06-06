package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.connection.ConnectionPool;
import com.github.fantasy0v0.swift.jdbc.connection.impl.DefaultConnectionPool;
import com.github.fantasy0v0.swift.jdbc.dialect.ANSI;
import com.github.fantasy0v0.swift.jdbc.dialect.SQLDialect;
import com.github.fantasy0v0.swift.jdbc.exception.SwiftException;
import com.github.fantasy0v0.swift.jdbc.type.*;
import com.github.fantasy0v0.swift.jdbc.util.LogUtil;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Supplier;

public final class JDBC {

  private static Context context;

  static {
    ConnectionPoolUtil.pool = ServiceLoader.load(ConnectionPool.class)
      .findFirst()
      .orElse(new DefaultConnectionPool());
    LogUtil.common().debug("使用的ConnectionPool: {}", ConnectionPoolUtil.pool.getClass().getName());
  }

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
    JDBC.context = context;
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
    Context context = new Context(dataSource, dialect, statementConfiguration);
    context.configure(new ByteTypeHandler());
    context.configure(new ShortTypeHandler());
    context.configure(new IntegerTypeHandler());
    context.configure(new FloatTypeHandler());
    context.configure(new DoubleTypeHandler());
    context.configure(new LongTypeHandler());
    context.configure(new BooleanTypeHandler());
    context.configure(new StringTypeHandler());
    context.configure(new TimestampTypeHandler());
    context.configure(new LocalTimeTypeHandler());
    context.configure(new LocalDateTypeHandler());
    context.configure(new LocalDateTimeTypeHandler());
    context.configure(new OffsetDateTimeTypeHandler());
    return context;
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

}
