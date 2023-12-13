package com.github.fantasy0v0.swift.jdbc;

import org.intellij.lang.annotations.Language;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Objects;

public final class JDBC {

  // private static final ScopedValue<Connection> CONN = ScopedValue.newInstance();

  private static DataSource dataSource;

  public static void configuration(DataSource dataSource) {
    JDBC.dataSource = dataSource;
  }

  public static SelectBuilder select(@Language("SQL") String sql, Object... params) {
    return new SelectBuilder(Objects.requireNonNull(dataSource), sql, params);
  }

}
