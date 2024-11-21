package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.exception.SwiftSQLException;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class UpdateBuilder implements StatementConfigurator<UpdateBuilder> {

  protected final DataSource dataSource;

  protected StatementConfiguration statementConfiguration;

  protected final String sql;

  protected ParameterHandler parameterHandler;

  UpdateBuilder(DataSource dataSource, StatementConfiguration statementConfiguration, String sql) {
    this.dataSource = dataSource;
    this.sql = sql;
    this.statementConfiguration = statementConfiguration;
  }

  protected StatementConfiguration getStatementConfiguration() {
    if (null == statementConfiguration) {
      statementConfiguration = new StatementConfiguration();
    }
    return statementConfiguration;
  }

  @Override
  public UpdateBuilder setQueryTimeout(Integer queryTimeout) {
    getStatementConfiguration().setQueryTimeout(queryTimeout);
    return this;
  }

  @Override
  public UpdateBuilder setFetchSize(Integer fetchSize) {
    getStatementConfiguration().setFetchSize(fetchSize);
    return this;
  }

  @Override
  public UpdateBuilder setMaxRows(Integer maxRows) {
    getStatementConfiguration().setMaxRows(maxRows);
    return this;
  }

  public UpdateBuilder setParameterHandler(ParameterHandler parameterHandler) {
    this.parameterHandler = parameterHandler;
    return this;
  }

  public int execute(List<Object> params) {
    try (ConnectionReference ref = ConnectionPoolUtil.getReference(dataSource)) {
      return Utils.executeUpdate(ref.unwrap(), statementConfiguration, sql, params, parameterHandler);
    } catch (SQLException e) {
      throw new SwiftSQLException(e);
    }
  }

  /**
   * 执行更新语句, 这里虽然叫execute, 但执行的是executeUpdate
   * @param params params
   * @return 受影响的行数
   */
  public int execute(Object... params) {
    return execute(Arrays.stream(params).toList());
  }

  /**
   * 执行更新语句, 这里虽然叫execute, 但执行的是executeUpdate
   * @return 受影响的行数
   */
  public int execute() {
    return execute((List<Object>) null);
  }

  public int[] batch(List<List<Object>> batchParams) {
    try (ConnectionReference ref = ConnectionPoolUtil.getReference(dataSource)) {
      return Utils.executeBatch(ref.unwrap(), statementConfiguration, sql, batchParams, parameterHandler);
    } catch (SQLException e) {
      throw new SwiftSQLException(e);
    }
  }

  private <T> List<T> _fetch(FetchMapper<T> mapper,
                             List<Object> params, boolean firstOnly) {
    try (ConnectionReference ref = ConnectionPoolUtil.getReference(dataSource)) {
      return Utils.execute(ref.unwrap(), statementConfiguration, sql, params, parameterHandler, mapper, firstOnly);
    } catch (SQLException e) {
      throw new SwiftSQLException(e);
    }
  }

  public <T> List<T> fetch(FetchMapper<T> mapper,
                           List<Object> params) {
    return _fetch(mapper, params, false);
  }

  public <T> List<T> fetch(FetchMapper<T> mapper, Object... params) {
    return fetch(mapper, Arrays.stream(params).toList());
  }

  public <T> List<T> fetch(FetchMapper<T> mapper) {
    return fetch(mapper, (List<Object>) null);
  }

  public List<Object[]> fetch(List<Object> params) {
    return fetch(Utils::fetchByRow, params);
  }

  public List<Object[]> fetch(Object... params) {
    return fetch(Utils::fetchByRow, Arrays.stream(params).toList());
  }

  public List<Object[]> fetch() {
    return fetch(Utils::fetchByRow, (List<Object>) null);
  }

  public <T> T fetchOne(FetchMapper<T> mapper,
                        List<Object> params) {
    List<T> list = _fetch(mapper, params, true);
    return (list == null || list.isEmpty()) ? null : list.getFirst();
  }

  public <T> T fetchOne(FetchMapper<T> mapper, Object... params) {
    return fetchOne(mapper, Arrays.stream(params).toList());
  }

  public <T> T fetchOne(FetchMapper<T> mapper) {
    return fetchOne(mapper, (List<Object>) null);
  }

  public Object[] fetchOne(List<Object> params) {
    return fetchOne(Utils::fetchByRow, params);
  }

  public Object[] fetchOne(Object... params) {
    return fetchOne(Utils::fetchByRow, Arrays.stream(params).toList());
  }

  public Object[] fetchOne() {
    return fetchOne(Utils::fetchByRow, (List<Object>) null);
  }

  /*public <T> List<T> fetchBatch(FetchMapper<T> mapper,
                                List<List<Object>> params) {
    try (ConnectionReference ref = ConnectionPoolUtil.getReference(dataSource)) {
      return Utils.executeBatch(ref.unwrap(), statementConfiguration, sql, params, parameterHandler, mapper);
    } catch (SQLException e) {
      throw new SwiftSQLException(e);
    }
  }

  public List<Object[]> fetchBatch(List<List<Object>> params) {
    return fetchBatch(Utils::fetchByRow, params);
  }*/

}
