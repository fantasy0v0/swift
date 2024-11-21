package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.connection.DefaultConnectionPool;
import com.github.fantasy0v0.swift.jdbc.dialect.ANSI;
import com.github.fantasy0v0.swift.jdbc.dialect.SQLDialect;
import com.github.fantasy0v0.swift.jdbc.exception.SwiftException;
import com.github.fantasy0v0.swift.jdbc.exception.SwiftSQLException;
import com.github.fantasy0v0.swift.jdbc.type.*;
import com.github.fantasy0v0.swift.jdbc.util.LogUtil;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class JDBC {

  private static DataSource dataSource;

  private static SQLDialect dialect;

  static final Map<Class<?>, TypeGetHandler<?>> GetHandlerMap = new ConcurrentHashMap<>();

  static final Map<Class<?>, TypeSetHandler<?>> SetHandlerMap = new ConcurrentHashMap<>();

  static {
    ConnectionPoolUtil.pool = ServiceLoader.load(ConnectionPool.class)
      .findFirst()
      .orElse(new DefaultConnectionPool());
    LogUtil.common().debug("使用的ConnectionPool: {}", ConnectionPoolUtil.pool.getClass().getName());

    configuration(new ByteTypeHandler());
    configuration(new ShortTypeHandler());
    configuration(new IntegerTypeHandler());
    configuration(new FloatTypeHandler());
    configuration(new DoubleTypeHandler());
    configuration(new LongTypeHandler());
    configuration(new BooleanTypeHandler());
    configuration(new StringTypeHandler());
    configuration(new TimestampTypeHandler());
    configuration(new LocalTimeTypeHandler());
    configuration(new LocalDateTypeHandler());
    configuration(new LocalDateTimeTypeHandler());
    configuration(new OffsetDateTimeTypeHandler());
  }

  static StatementConfiguration statementConfiguration;

  public static void configuration(DataSource dataSource) {
    JDBC.dataSource = dataSource;
  }

  public static DataSource getDataSource() {
    return JDBC.dataSource;
  }

  public static void configuration(SQLDialect dialect) {
    JDBC.dialect = dialect;
    LogUtil.common().debug("配置方言");
  }

  static SQLDialect getSQLDialect() {
    return null == dialect ? ANSI.Instance : dialect;
  }

  public static <T> void configuration(AbstractTypeHandler<T> typeHandler) {
    configuration((TypeGetHandler<T>)typeHandler);
    configuration((TypeSetHandler<T>)typeHandler);
  }

  public static <T> void configuration(TypeGetHandler<T> handler) {
    Class<T> supported = handler.support();
    if (GetHandlerMap.containsKey(supported)) {
      LogUtil.common().debug("{} 原有的GetHandler将被替换", supported);
    }
    GetHandlerMap.put(supported, handler);
  }

  public static <T> void configuration(TypeSetHandler<T> handler) {
    Class<T> supported = handler.support();
    if (SetHandlerMap.containsKey(supported)) {
      LogUtil.common().debug("{} 原有的SetHandler将被替换", supported);
    }
    SetHandlerMap.put(supported, handler);
  }

  public static void configuration(StatementConfiguration statementConfiguration) {
    JDBC.statementConfiguration = statementConfiguration;
    LogUtil.common().debug("配置Statement Configuration");
  }

  private static DataSource requireNonNull(DataSource dataSource) {
    if (null == dataSource) {
      throw new SwiftException("未配置DataSource");
    }
    return dataSource;
  }

  public static SelectBuilder select(String sql, List<Object> params) {
    return new SelectBuilder(requireNonNull(dataSource), statementConfiguration, sql.trim(), params);
  }

  public static SelectBuilder select(String sql, Object... params) {
    return select(sql, Arrays.stream(params).toList());
  }

  public static InsertBuilder insert(String sql) {
    return new InsertBuilder(requireNonNull(dataSource), statementConfiguration, sql.trim());
  }

  public static UpdateBuilder update(String sql) {
    return new UpdateBuilder(requireNonNull(dataSource), statementConfiguration, sql.trim());
  }

  public static void transaction(Integer level, Runnable runnable) {
    TransactionBuilder<?> builder = TransactionBuilder.create(dataSource, level, runnable);
    try {
      builder.execute();
    } catch (SQLException e) {
      throw new SwiftSQLException(e);
    }
  }

  public static void transaction(Runnable runnable) {
    transaction(null, runnable);
  }

  public static <T> T transaction(Integer level, Supplier<T> supplier) {
    TransactionBuilder<T> builder = TransactionBuilder.create(dataSource, level, supplier);
    try {
      return builder.execute();
    } catch (SQLException e) {
      throw new SwiftSQLException(e);
    }
  }

  public static <T> T transaction(Supplier<T> supplier) {
    return transaction(null, supplier);
  }

}
