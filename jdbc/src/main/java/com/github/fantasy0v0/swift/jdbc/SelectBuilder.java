package com.github.fantasy0v0.swift.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SelectBuilder {

  private final DataSource dataSource;

  private final String sql;

  private final Object[] params;

  SelectBuilder(DataSource dataSource, String sql, Object[] params) {
    this.dataSource = dataSource;
    this.sql = sql;
    this.params = params;
  }

  public PagingBuilder paging(Paging paging) {
    return new PagingBuilder(dataSource, sql, params);
  }

  private <T> List<T> _fetch(FetchMapper<T> mapper, ParameterProcess parameterProcess) throws SQLException {
    try (Connection conn = Utils.getConnection(dataSource)) {
      return Utils.executeQuery(conn, sql, params, parameterProcess, mapper);
    }
  }

  public <T> List<T> fetch(FetchMapper<T> mapper, ParameterProcess parameterProcess) {
    try {
      return _fetch(mapper, parameterProcess);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public <T> List<T> fetch(FetchMapper<T> mapper) {
    try {
      return fetch(mapper, null);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public List<Object> fetch() {
    return fetch(row -> {
      // TODO 返回Object
      return 1;
    });
  }

  public <T> T fetchOne(FetchMapper<T> mapper) {
    return null;
  }

}
