package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.exception.SwiftJdbcException;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ModifyBuilder {

  private final DataSource dataSource;

  private final String sql;

  ModifyBuilder(DataSource dataSource, String sql) {
    this.dataSource = dataSource;
    this.sql = sql;
  }

  public int execute(ParameterHandler parameterHandler, List<Object> params) {
    try (ConnectionReference ref = ConnectionReference.getReference(dataSource)) {
      return Utils.executeUpdate(ref.unwrap(), sql, params, parameterHandler);
    } catch (SQLException e) {
      throw new SwiftJdbcException(e);
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
    try (ConnectionReference ref = ConnectionReference.getReference(dataSource)) {
      return Utils.executeUpdateBatch(ref.unwrap(), sql, batch, parameterHandler);
    } catch (SQLException e) {
      throw new SwiftJdbcException(e);
    }
  }

  public int[] executeBatch(List<List<Object>> batch) {
    return executeBatch(null, batch);
  }

  public <T> T fetch(ParameterHandler parameterHandler,
                     FetchMapper<T> mapper,
                     List<Object> params) {
    try (ConnectionReference ref = ConnectionReference.getReference(dataSource)) {
      return Utils.execute(ref.unwrap(), sql, params, parameterHandler, mapper);
    } catch (SQLException e) {
      throw new SwiftJdbcException(e);
    }
  }

  public <T> T fetch(FetchMapper<T> mapper, List<Object> params) {
    return fetch(null, mapper, params);
  }

  public <T> T fetch(FetchMapper<T> mapper, Object... params) {
    return fetch(mapper, Arrays.stream(params).toList());
  }

  public <T> T fetch(FetchMapper<T> mapper) {
    return fetch(null, mapper, null);
  }

  public Object[] fetch(List<Object> params) {
    return fetch(null, Utils::fetchByRow, params);
  }

  public Object[] fetch(Object... params) {
    return fetch(Utils::fetchByRow, params);
  }

  public Object[] fetch() {
    return fetch(null, Utils::fetchByRow, null);
  }

  public <T> List<T> fetchBatch(ParameterHandler parameterHandler,
                                FetchMapper<T> mapper,
                                List<List<Object>> params) {
    try (ConnectionReference ref = ConnectionReference.getReference(dataSource)) {
      return Utils.executeBatch(ref.unwrap(), sql, params, parameterHandler, mapper);
    } catch (SQLException e) {
      throw new SwiftJdbcException(e);
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
