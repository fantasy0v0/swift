package com.github.fantasy0v0.swift.jdbc;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class Utils {

  static Connection getConnection(DataSource dataSource) throws SQLException {
    return dataSource.getConnection();
  }

  static <T> List<T> executeQuery(Connection conn, String sql, Object[] params,
                                  ParameterProcess parameterProcess,
                                  FetchMapper<T> fetchMapper) throws SQLException {
    LogUtil.performance().info("executeQuery begin");
    long startTime = System.nanoTime() / 1000;
    LogUtil.sql().debug("executeQuery: {}", sql);
    try (PreparedStatement statement = conn.prepareStatement(sql)) {
      fillStatementParams(conn, statement, params, parameterProcess);
      try (ResultSet resultSet = statement.executeQuery()) {
        List<T> array = new ArrayList<>();
        while (resultSet.next()) {
          T row = fetchMapper.apply(new Row(resultSet));
          array.add(row);
        }
        return array;
      }
    } finally {
      LogUtil.performance().info("executeQuery end. cost: {}μs", System.nanoTime() / 1000 - startTime);
    }
  }

  static boolean execute(Connection conn, String sql, Object[] params, ParameterProcess parameterProcess) throws SQLException {
    LogUtil.performance().info("execute begin");
    long startTime = System.nanoTime() / 1000;
    LogUtil.sql().debug("execute: {}", sql);
    try (PreparedStatement statement = conn.prepareStatement(sql)) {
      fillStatementParams(conn, statement, params, parameterProcess);
      return statement.execute();
    } finally {
      LogUtil.performance().info("execute end. cost: {}μs", System.nanoTime() / 1000 - startTime);
    }
  }

  static void fillStatementParams(Connection conn,
                                  PreparedStatement statement, Object[] params,
                                  ParameterProcess parameterProcess) throws SQLException {
    LogUtil.sql().trace("parameter count: {}", params.length);
    for (int index = 0; index < params.length; index++) {
      Object parameter = params[index];
      LogUtil.sql().trace("fill parameter: [{}] - [{}]", index, parameter);
      boolean result = false;
      if (null != parameterProcess) {
        result = parameterProcess.process(conn, statement, index, parameter);
      }
      // 使用默认的处理方法
      if (!result) {
        LogUtil.sql().debug("use default process");
        // TODO
      }
    }
  }

}
