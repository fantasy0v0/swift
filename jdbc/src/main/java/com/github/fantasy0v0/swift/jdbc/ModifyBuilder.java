package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.exception.SwiftSQLException;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Deprecated
public class ModifyBuilder {

  private final DataSource dataSource;

  private final String sql;

  ModifyBuilder(DataSource dataSource, String sql) {
    this.dataSource = dataSource;
    this.sql = sql;
  }

  public int execute(ParameterHandler parameterHandler, List<Object> params) {
    try (ConnectionReference ref = ConnectionPoolUtil.getReference(dataSource)) {
      return Utils.executeUpdate(ref.unwrap(), null, sql, params, parameterHandler);
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
      return Utils.executeBatch(ref.unwrap(), null, sql, batch, parameterHandler);
    } catch (SQLException e) {
      throw new SwiftSQLException(e);
    }
  }

  public int[] executeBatch(List<List<Object>> batch) {
    return executeBatch(null, batch);
  }

  public <T> List<T> fetch(ParameterHandler parameterHandler,
                     FetchMapper<T> mapper,
                     List<Object> params) {
    try (ConnectionReference ref = ConnectionPoolUtil.getReference(dataSource)) {
      return Utils.execute(ref.unwrap(), null, sql, params, parameterHandler, mapper, false);
    } catch (SQLException e) {
      throw new SwiftSQLException(e);
    }
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
    try (ConnectionReference ref = ConnectionPoolUtil.getReference(dataSource)) {
      List<T> list = Utils.execute(ref.unwrap(), null, sql, params, parameterHandler, mapper, true);
      return (list == null || list.isEmpty()) ? null : list.getFirst();
    } catch (SQLException e) {
      throw new SwiftSQLException(e);
    }
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

  public <T> List<T> fetchBatch(ParameterHandler parameterHandler,
                                FetchMapper<T> mapper,
                                List<List<Object>> params) {
    try (ConnectionReference ref = ConnectionPoolUtil.getReference(dataSource)) {
      return Utils.executeBatch(ref.unwrap(), null, sql, params, parameterHandler, mapper);
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
