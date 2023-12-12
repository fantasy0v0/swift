package com.github.fantasy0v0.swift.jdbc;

import javax.sql.DataSource;
import java.util.List;

public class PagingBuilder {

  private final DataSource dataSource;

  private final String sql;

  private final Object[] params;

  private String countSql;

  private Object[] countParams;

  PagingBuilder(DataSource dataSource, String sql, Object[] params) {
    this.dataSource = dataSource;
    this.sql = sql;
    this.params = params;
  }

  public PagingBuilder count(String countSql, Object...countParams) {
    this.countSql = countSql;
    this.countParams = countParams;
    return this;
  }

  public <T> List<T> fetch(FetchMapper<T> mapper, ParameterProcess parameterProcess) {
    return null;
  }

}
