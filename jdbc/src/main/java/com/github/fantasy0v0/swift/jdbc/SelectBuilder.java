package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.exception.SwiftSQLException;

import java.sql.SQLException;
import java.util.List;

public class SelectBuilder implements StatementConfigurator<SelectBuilder> {

  private final Context context;

  private StatementConfiguration statementConfiguration;

  private final String sql;

  private final List<Object> params;

  private ParameterHandler parameterHandler;

  SelectBuilder(Context context, String sql, List<Object> params) {
    this.context = context;
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

  public SelectBuilder setParameterHandler(ParameterHandler parameterHandler) {
    this.parameterHandler = parameterHandler;
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
    return new PaginateBuilder(context, parameterHandler, sql, params, number, size);
  }

  public <T> List<T> fetch(FetchMapper<T> mapper) {
    try {
      return Utils.fetch(dataSource, statementConfiguration, sql, params, mapper, parameterHandler);
    } catch (SQLException e) {
      throw new SwiftSQLException(e);
    }
  }

  public List<Object[]> fetch() {
    return fetch(Utils::fetchByRow);
  }

  public <T> T fetchOne(FetchMapper<T> mapper) {
    try {
      return Utils.fetchOne(dataSource, statementConfiguration, sql, params, mapper, parameterHandler);
    } catch (SQLException e) {
      throw new SwiftSQLException(e);
    }
  }

  public Object[] fetchOne() {
    return fetchOne(Utils::fetchByRow);
  }

}
