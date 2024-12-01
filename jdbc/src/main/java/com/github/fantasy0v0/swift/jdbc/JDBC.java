package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.connection.ConnectionPool;
import com.github.fantasy0v0.swift.jdbc.connection.impl.DefaultConnectionPool;
import com.github.fantasy0v0.swift.jdbc.dialect.SQLDialect;
import com.github.fantasy0v0.swift.jdbc.exception.SwiftException;
import com.github.fantasy0v0.swift.jdbc.exception.SwiftSQLException;
import com.github.fantasy0v0.swift.jdbc.type.*;
import com.github.fantasy0v0.swift.jdbc.util.LogUtil;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Supplier;

public final class JDBC {

  private static Context context;

  private static SQLDialect dialect;

  static {
    ConnectionPoolUtil.pool = ServiceLoader.load(ConnectionPool.class)
      .findFirst()
      .orElse(new DefaultConnectionPool());
    LogUtil.common().debug("使用的ConnectionPool: {}", ConnectionPoolUtil.pool.getClass().getName());
  }

  private static synchronized Context getContext() {
    if (null == context) {
      throw new SwiftException("请先初始化");
    }
    return context;
  }

  public static synchronized void initialization(DataSource dataSource, SQLDialect dialect) {
    if (null != context) {
      throw new SwiftException("请勿重复初始化");
    }
    context = new Context(dataSource, dialect);
    context.configuration(new ByteTypeHandler());
    context.configuration(new ShortTypeHandler());
    context.configuration(new IntegerTypeHandler());
    context.configuration(new FloatTypeHandler());
    context.configuration(new DoubleTypeHandler());
    context.configuration(new LongTypeHandler());
    context.configuration(new BooleanTypeHandler());
    context.configuration(new StringTypeHandler());
    context.configuration(new TimestampTypeHandler());
    context.configuration(new LocalTimeTypeHandler());
    context.configuration(new LocalDateTypeHandler());
    context.configuration(new LocalDateTimeTypeHandler());
    context.configuration(new OffsetDateTimeTypeHandler());
  }

  public static void configuration(DataSource dataSource) {
    getContext().configuration(dataSource);
  }

  public static void configuration(SQLDialect dialect) {
    getContext().configuration(dialect);
  }

  public static <T> void configuration(AbstractTypeHandler<T> typeHandler) {
    configuration((TypeGetHandler<T>)typeHandler);
    configuration((TypeSetHandler<T>)typeHandler);
  }

  public static <T> void configuration(TypeGetHandler<T> handler) {
    getContext().configuration(handler);
  }

  public static <T> void configuration(TypeSetHandler<T> handler) {
    getContext().configuration(handler);
  }

  public static void configuration(StatementConfiguration statementConfiguration) {
    getContext().configuration(statementConfiguration);
  }

  public static SelectBuilder select(String sql, List<Object> params) {
    return getContext().select(sql.trim(), params);
  }

  public static SelectBuilder select(String sql, Object... params) {
    return getContext().select(sql, Arrays.stream(params).toList());
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
