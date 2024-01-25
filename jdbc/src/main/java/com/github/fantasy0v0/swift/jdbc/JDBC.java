package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.dialect.ANSI;
import com.github.fantasy0v0.swift.jdbc.dialect.SQLDialect;
import com.github.fantasy0v0.swift.jdbc.exception.SwiftJdbcException;
import com.github.fantasy0v0.swift.jdbc.typehandles.*;
import org.intellij.lang.annotations.Language;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    configuration(new LocalTimeTypeHandler());
    configuration(new LocalDateTypeHandler());
    configuration(new LocalDateTimeTypeHandler());
  }

  public static void configuration(DataSource dataSource) {
    JDBC.dataSource = dataSource;
  }

  public static DataSource getDataSource() {
    return JDBC.dataSource;
  }

  public static void configuration(SQLDialect dialect) {
    JDBC.dialect = dialect;
  }

  static SQLDialect getSQLDialect() {
    return null == dialect ? ANSI.Instance : dialect;
  }

  public static <T> void configuration(TypeHandler<T> typeHandler) {
    handlerMap.put(typeHandler.supported(), typeHandler);
  }

  private static DataSource requireNonNull(DataSource dataSource) {
    if (null == dataSource) {
      throw new SwiftJdbcException("未配置DataSource");
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
      throw new SwiftJdbcException(e);
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
      throw new SwiftJdbcException(e);
    }
  }

  public static <T> T transaction(Supplier<T> supplier) {
    return transaction(null, supplier);
  }

}
