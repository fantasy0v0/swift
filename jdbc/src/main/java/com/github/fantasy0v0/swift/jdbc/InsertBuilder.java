package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.exception.SwiftJdbcException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class InsertBuilder {

  private final DataSource dataSource;

  private final String sql;

  InsertBuilder(DataSource dataSource, String sql) {
    this.dataSource = dataSource;
    this.sql = sql;
  }

  public boolean execute(ParameterProcess parameterProcess, List<Object> params) {
    try (Connection conn = Utils.getConnection(dataSource)) {
      return Utils.execute(conn, sql, params, parameterProcess);
    } catch (SQLException e) {
      throw new SwiftJdbcException(e);
    }
  }

  public boolean execute(ParameterProcess parameterProcess, Object... params) {
    try (Connection conn = Utils.getConnection(dataSource)) {
      return Utils.execute(conn, sql, List.of(params), parameterProcess);
    } catch (SQLException e) {
      throw new SwiftJdbcException(e);
    }
  }

  public boolean execute(List<Object> params) {
    return execute(null, params);
  }

  public boolean execute(Object... params) {
    return execute(null, List.of(params));
  }

  public boolean execute() {
    return execute(null, (List<Object>) null);
  }

  public int[] executeBatch(ParameterProcess parameterProcess, List<List<Object>> batch) {
    try (Connection conn = Utils.getConnection(dataSource)) {
      return Utils.executeBatch(conn, sql, batch, parameterProcess);
    } catch (SQLException e) {
      throw new SwiftJdbcException(e);
    }
  }

  public int[] executeBatch(List<List<Object>> batch) {
    return executeBatch(null, batch);
  }
}
