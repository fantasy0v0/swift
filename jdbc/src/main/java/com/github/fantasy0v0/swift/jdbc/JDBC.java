package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.exception.SwiftJdbcException;
import org.intellij.lang.annotations.Language;

import javax.sql.DataSource;
import java.util.List;

public final class JDBC {

  // private static final ScopedValue<Connection> CONN = ScopedValue.newInstance();

  private static DataSource dataSource;

  public static void configuration(DataSource dataSource) {
    JDBC.dataSource = dataSource;
  }

  public static SelectBuilder select(@Language("SQL") String sql, List<Object> params) {
    return new SelectBuilder(requireNonNull(dataSource), sql, params);
  }

  public static SelectBuilder select(@Language("SQL") String sql, Object... params) {
    return select(sql, List.of(params));
  }

  private static DataSource requireNonNull(DataSource dataSource) {
    if (null == dataSource) {
      throw new SwiftJdbcException("未配置DataSource");
    }
    return dataSource;
  }

}
