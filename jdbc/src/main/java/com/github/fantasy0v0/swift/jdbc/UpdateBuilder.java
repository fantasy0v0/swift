package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.exception.SwiftSQLException;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class UpdateBuilder implements StatementConfigurator<UpdateBuilder> {

  private final DataSource dataSource;

  private StatementConfiguration statementConfiguration;

  private final String sql;

  UpdateBuilder(DataSource dataSource, StatementConfiguration statementConfiguration, String sql) {
    this.dataSource = dataSource;
    this.sql = sql;
    this.statementConfiguration = statementConfiguration;
  }

  private StatementConfiguration getStatementConfiguration() {
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
  public UpdateBuilder setMaxFieldSize(Integer maxFieldSize) {
    getStatementConfiguration().setMaxFieldSize(maxFieldSize);
    return this;
  }

  @Override
  public UpdateBuilder setMaxRows(Integer maxRows) {
    getStatementConfiguration().setMaxRows(maxRows);
    return this;
  }

  public int execute(ParameterHandler parameterHandler, List<Object> params) {
    try (ConnectionReference ref = ConnectionPoolUtil.getReference(dataSource)) {
      return Utils.executeUpdate(ref.unwrap(), statementConfiguration, sql, params, parameterHandler);
    } catch (SQLException e) {
      throw new SwiftSQLException(e);
    }
  }

  public int execute(ParameterHandler parameterHandler, Object... params) {
    return execute(parameterHandler, Arrays.stream(params).toList());
  }

  public int execute(List<Object> params) {
    return execute(null, params);
  }

  public int execute(Object... params) {
    return execute(null, Arrays.stream(params).toList());
  }

  public int execute() {
    return execute(null, (List<Object>) null);
  }

  public int[] executeBatch(ParameterHandler parameterHandler, List<List<Object>> batch) {
    try (ConnectionReference ref = ConnectionPoolUtil.getReference(dataSource)) {
      return Utils.executeUpdateBatch(ref.unwrap(), statementConfiguration, sql, batch, parameterHandler);
    } catch (SQLException e) {
      throw new SwiftSQLException(e);
    }
  }

  public int[] executeBatch(List<List<Object>> batch) {
    return executeBatch(null, batch);
  }

  private <T> List<T> _fetch(ParameterHandler parameterHandler,
                           FetchMapper<T> mapper,
                           List<Object> params, boolean firstOnly) {
    try (ConnectionReference ref = ConnectionPoolUtil.getReference(dataSource)) {
      return Utils.execute(ref.unwrap(), statementConfiguration, sql, params, parameterHandler, mapper, firstOnly);
    } catch (SQLException e) {
      throw new SwiftSQLException(e);
    }
  }

  public <T> List<T> fetch(ParameterHandler parameterHandler,
                           FetchMapper<T> mapper,
                           List<Object> params) {
    return _fetch(parameterHandler, mapper, params, false);
  }

  public <T> List<T> fetch(FetchMapper<T> mapper, List<Object> params) {
    return fetch(null, mapper, params);
  }

  public <T> List<T> fetch(FetchMapper<T> mapper, Object... params) {
    return fetch(mapper, Arrays.stream(params).toList());
  }

  public <T> List<T> fetch(FetchMapper<T> mapper) {
    return fetch(null, mapper, null);
  }

  public List<Object[]> fetch(List<Object> params) {
    return fetch(null, Utils::fetchByRow, params);
  }

  public List<Object[]> fetch(Object... params) {
    return fetch(Utils::fetchByRow, params);
  }

  public List<Object[]> fetch() {
    return fetch(null, Utils::fetchByRow, null);
  }

  public <T> T fetchOne(ParameterHandler parameterHandler,
                        FetchMapper<T> mapper,
                        List<Object> params) {
    List<T> list = _fetch(parameterHandler, mapper, params, true);
    return (list == null || list.isEmpty()) ? null : list.getFirst();
  }

  public <T> T fetchOne(FetchMapper<T> mapper, List<Object> params) {
    return fetchOne(null, mapper, params);
  }

  public <T> T fetchOne(FetchMapper<T> mapper, Object... params) {
    return fetchOne(mapper, Arrays.stream(params).toList());
  }

  public <T> T fetchOne(FetchMapper<T> mapper) {
    return fetchOne(null, mapper, null);
  }

  public Object[] fetchOne(List<Object> params) {
    return fetchOne(null, Utils::fetchByRow, params);
  }

  public Object[] fetchOne(Object... params) {
    return fetchOne(Utils::fetchByRow, params);
  }

  public Object[] fetchOne() {
    return fetchOne(null, Utils::fetchByRow, null);
  }

  // TODO
  public <T> List<T> fetchBatch(ParameterHandler parameterHandler,
                                FetchMapper<T> mapper,
                                List<List<Object>> params) {
    try (ConnectionReference ref = ConnectionPoolUtil.getReference(dataSource)) {
      return Utils.executeBatch(ref.unwrap(), sql, params, parameterHandler, mapper);
    } catch (SQLException e) {
      throw new SwiftSQLException(e);
    }
  }

  public <T> List<T> fetchBatch(FetchMapper<T> mapper, List<List<Object>> params) {
    return fetchBatch(null, mapper, params);
  }

  public <T> List<T> fetchBatch(FetchMapper<T> mapper) {
    return fetchBatch(null, mapper, null);
  }

  public List<Object[]> fetchBatch() {
    return fetchBatch(null, Utils::fetchByRow, null);
  }

}
