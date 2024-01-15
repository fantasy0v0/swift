package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.dialect.ANSI;
import com.github.fantasy0v0.swift.jdbc.dialect.SQLDialect;
import com.github.fantasy0v0.swift.jdbc.exception.SwiftJdbcException;
import org.intellij.lang.annotations.Language;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public final class JDBC {

  private static DataSource dataSource;

  private static SQLDialect dialect;

  public static void configuration(DataSource dataSource) {
    JDBC.dataSource = dataSource;
  }

  public static void configuration(SQLDialect dialect) {
    JDBC.dialect = dialect;
  }

  static SQLDialect getSQLDialect() {
    return null == dialect ? ANSI.Instance : dialect;
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
    TransactionBuilder builder = new TransactionBuilder(requireNonNull(dataSource), level, runnable);
    try {
      builder.execute();
    } catch (SQLException e) {
      throw new SwiftJdbcException(e);
    }
  }

  public static void transaction(Runnable runnable) {
    transaction(null, runnable);
  }

}
