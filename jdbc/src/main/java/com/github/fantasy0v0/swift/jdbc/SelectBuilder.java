package com.github.fantasy0v0.swift.jdbc;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

public class SelectBuilder {

  private final DataSource dataSource;

  private final String sql;

  private final List<Object> params;

  SelectBuilder(DataSource dataSource, String sql, List<Object> params) {
    this.dataSource = dataSource;
    this.sql = sql;
    this.params = params;
  }

  public PagingBuilder paging(Paging paging) {
    return new PagingBuilder(dataSource, sql, params);
  }

  public <T> List<T> fetch(FetchMapper<T> mapper, ParameterProcess parameterProcess) {
    try {
      return Utils._fetch(dataSource, sql, params, mapper, parameterProcess, false);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public <T> List<T> fetch(FetchMapper<T> mapper) {
    return fetch(mapper, null);
  }

  private Object[] getRowByObject(Row row) throws SQLException {
    int columnCount = row.getColumnCount();
    Object[] array = new Object[columnCount];
    for (int i = 0; i < columnCount; i++) {
      array[i] = row.getObject(i + 1);
    }
    return array;
  }

  public List<Object[]> fetch() {
    return fetch(this::getRowByObject);
  }

  public <T> T fetchOne(FetchMapper<T> mapper, ParameterProcess parameterProcess) {
    try {
      List<T> list = Utils._fetch(dataSource, sql, params, mapper, parameterProcess, true);
      return list.isEmpty() ? null : list.getFirst();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public <T> T fetchOne(FetchMapper<T> mapper) {
    return fetchOne(mapper, null);
  }

  public Object[] fetchOne() {
    return fetchOne(this::getRowByObject);
  }

}
