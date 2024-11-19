package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.exception.SwiftSQLException;
import com.github.fantasy0v0.swift.jdbc.util.LogUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.fantasy0v0.swift.jdbc.Utils.*;

public class InsertBuilder extends UpdateBuilder {

  InsertBuilder(DataSource dataSource, StatementConfiguration statementConfiguration, String sql) {
    super(dataSource, statementConfiguration, sql);
  }

  private <T> List<T> _fetchKey(FetchMapper<T> mapper,
                             List<Object> params, boolean firstOnly) {
    try (ConnectionReference ref = ConnectionPoolUtil.getReference(dataSource)) {
      Connection conn = ref.unwrap();
      LogUtil.performance().info("fetchKey begin");
      long startTime = System.nanoTime() / 1000;
      String callerInfo = printCallerInfo();
      LogUtil.sql().debug("fetchKey: [{}], caller: {}", sql, callerInfo);
      try (PreparedStatement statement = prepareStatement(conn, sql, PreparedStatement.RETURN_GENERATED_KEYS, statementConfiguration)) {
        fillStatementParams(conn, statement, params, parameterHandler);
        int updated = statement.executeUpdate();
        LogUtil.sql().debug("executeUpdate: {}", updated);
        return fetchByResultSet(statement.getGeneratedKeys(), mapper, firstOnly);
      } finally {
        long cost = System.nanoTime() / 1000 - startTime;
        NumberFormat format = NumberFormat.getNumberInstance();
        LogUtil.performance().info("fetchKey end, cost: {} μs", format.format(cost));
      }
    } catch (SQLException e) {
      throw new SwiftSQLException(e);
    }
  }

  public <T> List<T> fetchKey(FetchMapper<T> mapper, List<Object> params) {
    return _fetchKey(mapper, params, false);
  }

  public <T> List<T> fetchKey(FetchMapper<T> mapper, Object... params) {
    return fetchKey(mapper, Arrays.stream(params).toList());
  }

  public <T> List<T> fetchKey(FetchMapper<T> mapper) {
    return fetchKey(mapper, (List<Object>) null);
  }

  public List<Object[]> fetchKey(List<Object> params) {
    return fetchKey(Utils::fetchByRow, params);
  }

  public List<Object[]> fetchKey(Object... params) {
    return fetchKey(Arrays.stream(params).toList());
  }

  public List<Object[]> fetchKey() {
    return fetchKey(Utils::fetchByRow, (List<Object>) null);
  }

  public <T> T fetchKeyOne(FetchMapper<T> mapper, List<Object> params) {
    List<T> list = _fetchKey(mapper, params, true);
    return list.isEmpty() ? null : list.getFirst();
  }

  public <T> T fetchKeyOne(FetchMapper<T> mapper, Object... params) {
    return fetchKeyOne(mapper, Arrays.stream(params).toList());
  }

  public <T> T fetchKeyOne(FetchMapper<T> mapper) {
    return fetchKeyOne(mapper, (List<Object>) null);
  }

  public Object[] fetchKeyOne(List<Object> params) {
    return fetchKeyOne(Utils::fetchByRow, params);
  }

  public Object[] fetchKeyOne(Object... params) {
    return fetchKeyOne(Arrays.stream(params).toList());
  }

  public Object[] fetchKeyOne() {
    return fetchKeyOne((List<Object>) null);
  }
}
