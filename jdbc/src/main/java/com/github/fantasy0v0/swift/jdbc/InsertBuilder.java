package com.github.fantasy0v0.swift.jdbc;

import javax.sql.DataSource;
import java.util.List;

public class InsertBuilder {

  private final DataSource dataSource;

  private final String sql;

  private final List<Object> params;

  InsertBuilder(DataSource dataSource, String sql, List<Object> params) {
    this.dataSource = dataSource;
    this.sql = sql;
    this.params = params;
  }
}
