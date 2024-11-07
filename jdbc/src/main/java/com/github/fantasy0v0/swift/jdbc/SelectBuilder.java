package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.exception.SwiftSQLException;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

public class SelectBuilder implements StatementConfigurator<SelectBuilder> {

  private final DataSource dataSource;

  private StatementConfiguration statementConfiguration;

  private final String sql;

  private final List<Object> params;

  SelectBuilder(DataSource dataSource,
                StatementConfiguration statementConfiguration,
                String sql, List<Object> params) {
    this.dataSource = dataSource;
    this.statementConfiguration = statementConfiguration;
    this.sql = sql;
    this.params = params;
  }

  private StatementConfiguration getStatementConfiguration() {
    if (null == statementConfiguration) {
      statementConfiguration = new StatementConfiguration();
    }
    return statementConfiguration;
  }

  @Override
  public SelectBuilder setQueryTimeout(Integer queryTimeout) {
    getStatementConfiguration().setQueryTimeout(queryTimeout);
    return this;
  }

  @Override
  public SelectBuilder setFetchSize(Integer fetchSize) {
    getStatementConfiguration().setFetchSize(fetchSize);
    return this;
  }

  @Override
  public SelectBuilder setMaxRows(Integer maxRows) {
    getStatementConfiguration().setMaxRows(maxRows);
    return this;
  }

  /**
   * 进行分页
   *
   * @param number 页码从0开始
   * @param size   每页大小
   * @return PaginateBuilder
   */
  public PaginateBuilder paginate(long number, long size) {
    return new PaginateBuilder(dataSource, statementConfiguration, sql, params, number, size);
  }

  public <T> List<T> fetch(FetchMapper<T> mapper, ParameterHandler parameterHandler) {
    try {
      return Utils.fetch(dataSource, statementConfiguration, sql, params, mapper, parameterHandler);
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
      return Utils.fetchOne(dataSource, statementConfiguration, sql, params, mapper, parameterHandler);
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
