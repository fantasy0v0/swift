package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.connection.DefaultConnectionPool;
import com.github.fantasy0v0.swift.jdbc.dialect.ANSI;
import com.github.fantasy0v0.swift.jdbc.dialect.SQLDialect;
import com.github.fantasy0v0.swift.jdbc.exception.SwiftException;
import com.github.fantasy0v0.swift.jdbc.exception.SwiftSQLException;
import com.github.fantasy0v0.swift.jdbc.typehandles.*;
import com.github.fantasy0v0.swift.jdbc.util.LogUtil;
import org.intellij.lang.annotations.Language;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Supplier;

public final class JDBC {

  private static DataSource dataSource;

  private static SQLDialect dialect;

  static final Map<Class<?>, TypeHandler<?>> handlerMap = new HashMap<>();

  static {
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

  public static void configuration(DataSource dataSource) {
    JDBC.dataSource = dataSource;
    ConnectionPoolUtil.pool = ServiceLoader.load(ConnectionPool.class)
      .findFirst()
      .orElse(new DefaultConnectionPool());
    LogUtil.common().debug("使用的ConnectionPool: {}", ConnectionPoolUtil.pool.getClass().getName());
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

  public static <T> void configuration(TypeHandler<T> typeHandler) {
    Class<T> supported = typeHandler.supported();
    if (handlerMap.containsKey(supported)) {
      LogUtil.common().debug("原有的 {} handler将被替换", supported);
    }
    handlerMap.put(typeHandler.supported(), typeHandler);
  }

  private static DataSource requireNonNull(DataSource dataSource) {
    if (null == dataSource) {
      throw new SwiftException("未配置DataSource");
    }
    return dataSource;
  }

  public static SelectBuilder select(@Language("SQL") String sql, List<Object> params) {
    return new SelectBuilder(requireNonNull(dataSource), sql.trim(), params);
  }

  public static SelectBuilder select(@Language("SQL") String sql, Object... params) {
    return select(sql, Arrays.stream(params).toList());
  }

  public static ModifyBuilder modify(@Language("SQL") String sql) {
    return new ModifyBuilder(requireNonNull(dataSource), sql.trim());
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
