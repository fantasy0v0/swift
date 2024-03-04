package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.exception.SwiftSQLException;

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

  /**
   * 进行分页
   *
   * @param number 页码从0开始
   * @param size   每页大小
   * @return PagingBuilder
   */
  public PagingBuilder paging(long number, long size) {
    return new PagingBuilder(dataSource, sql, params, number, size);
  }

  public <T> List<T> fetch(FetchMapper<T> mapper, ParameterHandler parameterHandler) {
    try {
      return Utils.fetch(dataSource, sql, params, mapper, parameterHandler);
    } catch (SQLException e) {
      throw new SwiftSQLException(e);
    }
  }

  public <T> List<T> fetch(FetchMapper<T> mapper) {
    return fetch(mapper, null);
  }

  public List<Object[]> fetch() {
    return fetch(Utils::fetchByRow);
  }

  public <T> T fetchOne(FetchMapper<T> mapper, ParameterHandler parameterHandler) {
    try {
      return Utils.fetchOne(dataSource, sql, params, mapper, parameterHandler);
    } catch (SQLException e) {
      throw new SwiftSQLException(e);
    }
  }

  public <T> T fetchOne(FetchMapper<T> mapper) {
    return fetchOne(mapper, null);
  }

  public Object[] fetchOne() {
    return fetchOne(Utils::fetchByRow);
  }

}
