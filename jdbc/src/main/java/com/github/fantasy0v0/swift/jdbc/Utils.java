package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.util.LogUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

final class Utils {

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
                           FetchMapper<T> mapper, ParameterProcess parameterProcess) throws SQLException {
    try (Connection conn = Utils.getConnection(dataSource)) {
      return executeQuery(conn, sql, params, mapper, parameterProcess, false);
    }
  }

  static <T> T fetchOne(DataSource dataSource,
                        String sql, List<Object> params,
                        FetchMapper<T> mapper, ParameterProcess parameterProcess) throws SQLException {
    try (Connection conn = Utils.getConnection(dataSource)) {
      List<T> list = executeQuery(conn, sql, params, mapper, parameterProcess, true);
      return list.isEmpty() ? null : list.getFirst();
    }
  }

  static <T> List<T> executeQuery(Connection conn,
                                  String sql, List<Object> params,
                                  FetchMapper<T> fetchMapper,
                                  ParameterProcess parameterProcess,
                                  boolean firstOnly) throws SQLException {
    LogUtil.performance().info("executeQuery begin");
    long startTime = System.nanoTime() / 1000;
    LogUtil.sql().debug("executeQuery: {}", sql);
    try (PreparedStatement statement = conn.prepareStatement(sql)) {
      fillStatementParams(conn, statement, params, parameterProcess);
      try (ResultSet resultSet = statement.executeQuery()) {
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
    } finally {
      long cost = System.nanoTime() / 1000 - startTime;
      NumberFormat format = NumberFormat.getNumberInstance();
      LogUtil.performance().info("executeQuery end, cost: {} μs", format.format(cost));
    }
  }

  static boolean execute(Connection conn,
                         String sql, List<Object> params,
                         ParameterProcess parameterProcess) throws SQLException {
    LogUtil.performance().info("execute begin");
    long startTime = System.nanoTime() / 1000;
    LogUtil.sql().debug("execute: {}", sql);
    try (PreparedStatement statement = conn.prepareStatement(sql)) {
      fillStatementParams(conn, statement, params, parameterProcess);
      return statement.execute();
    } finally {
      long cost = System.nanoTime() / 1000 - startTime;
      NumberFormat format = NumberFormat.getNumberInstance();
      LogUtil.performance().info("execute end, cost: {} μs", format.format(cost));
    }
  }

  static int[] executeBatch(Connection conn,
                            String sql, List<List<Object>> batch,
                            ParameterProcess parameterProcess) throws SQLException {
    LogUtil.performance().info("execute begin");
    long startTime = System.nanoTime() / 1000;
    LogUtil.sql().debug("execute: {}", sql);
    try (PreparedStatement statement = conn.prepareStatement(sql)) {
      for (List<Object> params : batch) {
        fillStatementParams(conn, statement, params, parameterProcess);
        statement.addBatch();
      }
      return statement.executeBatch();
    } finally {
      long cost = System.nanoTime() / 1000 - startTime;
      NumberFormat format = NumberFormat.getNumberInstance();
      LogUtil.performance().info("execute end, cost: {} μs", format.format(cost));
    }
  }

  static int executeUpdate(Connection conn,
                           String sql, List<Object> params,
                           ParameterProcess parameterProcess) throws SQLException {
    LogUtil.performance().info("execute begin");
    long startTime = System.nanoTime() / 1000;
    LogUtil.sql().debug("execute: {}", sql);
    try (PreparedStatement statement = conn.prepareStatement(sql)) {
      fillStatementParams(conn, statement, params, parameterProcess);
      return statement.executeUpdate();
    } finally {
      long cost = System.nanoTime() / 1000 - startTime;
      NumberFormat format = NumberFormat.getNumberInstance();
      LogUtil.performance().info("execute end, cost: {} μs", format.format(cost));
    }
  }

  static void fillStatementParams(Connection conn,
                                  PreparedStatement statement, List<Object> params,
                                  ParameterProcess parameterProcess) throws SQLException {
    if (null == params) {
      LogUtil.sql().debug("parameter is null");
    }
    LogUtil.sql().trace("parameter count: {}", params.size());
    for (int index = 0; index < params.size(); index++) {
      Object parameter = params.get(index);

      boolean result = false;
      if (null != parameterProcess) {
        result = parameterProcess.process(conn, statement, index + 1, parameter);
      }
      // 使用默认的处理方法
      if (result) {
        LogUtil.sql().trace("fill parameter: [{}] - [{}], use parameterProcess", index + 1, parameter);
      } else {
        LogUtil.sql().trace("fill parameter: [{}] - [{}], use default process", index + 1, parameter);
        if (parameter instanceof Integer param) {
          statement.setInt(index + 1, param);
        } else if (parameter instanceof Long param) {
          statement.setLong(index + 1, param);
        } else {
          LogUtil.sql().debug("fill parameter use setObject, parameter class:{}", parameter.getClass());
          statement.setObject(index + 1, parameter);
        }
      }
    }
  }

}
