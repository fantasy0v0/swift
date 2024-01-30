package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.util.LogUtil;

import javax.sql.DataSource;
import java.sql.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

final class Utils {

  @Deprecated
  static Connection getConnection(DataSource dataSource) throws SQLException {
    return dataSource.getConnection();
  }

  static Object[] fetchByRow(Row row) throws SQLException {
    int columnCount = row.getColumnCount();
    Object[] array = new Object[columnCount];
    for (int i = 0; i < columnCount; i++) {
      array[i] = row.getObject(i + 1);
    }
    return array;
  }

  static <T> List<T> fetch(DataSource dataSource,
                           String sql, List<Object> params,
                           FetchMapper<T> mapper, ParameterHandler parameterHandler) throws SQLException {

    try (ConnectionReference ref = ConnectionReference.getReference(dataSource)) {
      return executeQuery(ref.unwrap(), sql, params, mapper, parameterHandler, false);
    }
  }

  static <T> T fetchOne(DataSource dataSource,
                        String sql, List<Object> params,
                        FetchMapper<T> mapper, ParameterHandler parameterHandler) throws SQLException {
    try (ConnectionReference ref = ConnectionReference.getReference(dataSource)) {
      List<T> list = executeQuery(ref.unwrap(), sql, params, mapper, parameterHandler, true);
      return list.isEmpty() ? null : list.getFirst();
    }
  }

  static <T> List<T> fetchByResultSet(ResultSet resultSet,
                                      FetchMapper<T> fetchMapper,
                                      boolean firstOnly) throws SQLException {
    List<T> array = new ArrayList<>();
    boolean first = true;
    while (resultSet.next()) {
      T row = fetchMapper.apply(new Row(resultSet));
      array.add(row);
      if (first && firstOnly) {
        break;
      }
      first = false;
    }
    return array;
  }

  static <T> List<T> executeQuery(Connection conn,
                                  String sql, List<Object> params,
                                  FetchMapper<T> fetchMapper,
                                  ParameterHandler parameterHandler,
                                  boolean firstOnly) throws SQLException {
    LogUtil.performance().info("executeQuery begin");
    long startTime = System.nanoTime() / 1000;
    LogUtil.sql().debug("executeQuery: {}", sql);
    try (PreparedStatement statement = conn.prepareStatement(sql)) {
      fillStatementParams(conn, statement, params, parameterHandler);
      try (ResultSet resultSet = statement.executeQuery()) {
        return fetchByResultSet(resultSet, fetchMapper, firstOnly);
      }
    } finally {
      long cost = System.nanoTime() / 1000 - startTime;
      NumberFormat format = NumberFormat.getNumberInstance();
      LogUtil.performance().info("executeQuery end, cost: {} μs", format.format(cost));
    }
  }

  static <T> List<T> execute(Connection conn,
                       String sql, List<Object> params,
                       ParameterHandler parameterHandler,
                             FetchMapper<T> mapper, boolean firstOnly) throws SQLException {
    LogUtil.performance().info("execute begin");
    long startTime = System.nanoTime() / 1000;
    LogUtil.sql().debug("execute: {}", sql);
    try (PreparedStatement statement = conn.prepareStatement(sql)) {
      fillStatementParams(conn, statement, params, parameterHandler);
      boolean result = statement.execute();
      if (!result) {
        return null;
      }
      try (ResultSet resultSet = statement.getResultSet()) {
        return fetchByResultSet(resultSet, mapper, firstOnly);
      }
    } finally {
      long cost = System.nanoTime() / 1000 - startTime;
      NumberFormat format = NumberFormat.getNumberInstance();
      LogUtil.performance().info("execute end, cost: {} μs", format.format(cost));
    }
  }

  static <T> List<T> executeBatch(Connection conn,
                            String sql, List<List<Object>> batch,
                                  ParameterHandler parameterHandler,
                                  FetchMapper<T> mapper) throws SQLException {
    LogUtil.performance().info("executeBatch begin");
    long startTime = System.nanoTime() / 1000;
    LogUtil.sql().debug("executeBatch: {}", sql);
    try (PreparedStatement statement = conn.prepareStatement(sql)) {
      for (List<Object> params : batch) {
        fillStatementParams(conn, statement, params, parameterHandler);
        statement.addBatch();
      }
      List<T> list = new ArrayList<>();
      while (statement.getMoreResults()) {
        try (ResultSet resultSet = statement.getResultSet()) {
          List<T> row = fetchByResultSet(resultSet, mapper, true);
          if (!row.isEmpty()) {
            list.add(row.getFirst());
          }
        }
      }
      return list;
    } finally {
      long cost = System.nanoTime() / 1000 - startTime;
      NumberFormat format = NumberFormat.getNumberInstance();
      LogUtil.performance().info("executeBatch end, cost: {} μs", format.format(cost));
    }
  }

  static int executeUpdate(Connection conn,
                           String sql, List<Object> params,
                           ParameterHandler parameterHandler) throws SQLException {
    LogUtil.performance().info("executeUpdate begin");
    long startTime = System.nanoTime() / 1000;
    LogUtil.sql().debug("executeUpdate: {}", sql);
    try (PreparedStatement statement = conn.prepareStatement(sql)) {
      fillStatementParams(conn, statement, params, parameterHandler);
      return statement.executeUpdate();
    } finally {
      long cost = System.nanoTime() / 1000 - startTime;
      NumberFormat format = NumberFormat.getNumberInstance();
      LogUtil.performance().info("executeUpdate end, cost: {} μs", format.format(cost));
    }
  }

  static int[] executeUpdateBatch(Connection conn,
                                  String sql, List<List<Object>> batch,
                                  ParameterHandler parameterHandler) throws SQLException {
    LogUtil.performance().info("execute begin");
    long startTime = System.nanoTime() / 1000;
    LogUtil.sql().debug("execute: {}", sql);
    try (PreparedStatement statement = conn.prepareStatement(sql)) {
      for (List<Object> params : batch) {
        fillStatementParams(conn, statement, params, parameterHandler);
        statement.addBatch();
      }
      return statement.executeBatch();
    } finally {
      long cost = System.nanoTime() / 1000 - startTime;
      NumberFormat format = NumberFormat.getNumberInstance();
      LogUtil.performance().info("execute end, cost: {} μs", format.format(cost));
    }
  }

  @SuppressWarnings("unchecked")
  static void fillStatementParams(Connection conn,
                                  PreparedStatement statement, List<Object> params,
                                  ParameterHandler parameterHandler) throws SQLException {
    if (null == params) {
      LogUtil.sql().debug("parameter is null");
      return;
    }
    LogUtil.sql().trace("parameter count: {}", params.size());
    for (int index = 0; index < params.size(); index++) {
      Object parameter = params.get(index);

      // 优先使用参数中的parameterHandler
      boolean result = false;
      if (null != parameterHandler) {
        result = parameterHandler.handle(conn, statement, index + 1, parameter);
      }

      if (result) {
        LogUtil.sql().trace("fill parameter: [{}] - [{}], use method parameter handler", index + 1, parameter);
      } else {
        TypeHandler<?> handler;
        if (null != parameter) {
          handler = JDBC.handlerMap.get(parameter.getClass());
          if (null != handler) {
            result = ((TypeHandler<Object>) handler).handle(conn, statement, index + 1, parameter);
            if (result) {
              LogUtil.sql().trace("fill parameter: [{}] - [{}], use global parameter handler", index + 1, parameter);
              continue;
            }
          }
          LogUtil.sql().debug("fill parameter: [{}] - [{}], use setObject", index + 1, parameter.getClass());
          statement.setObject(index + 1, parameter);
        } else {
          LogUtil.sql().debug("fill parameter: [{}] - [null], use setNull", index + 1);
          statement.setNull(index + 1, Types.NULL);
        }
      }
    }
  }

}
