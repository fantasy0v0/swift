package com.github.fantasy0v0.swift.jdbc;

public final class JdbcUtils {

  public static SelectBuilder select(String sql, Object... params) {
    return new SelectBuilder(sql, params);
  }

}
