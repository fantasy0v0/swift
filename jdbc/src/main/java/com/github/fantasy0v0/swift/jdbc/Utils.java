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

  static <T> List<T> executeQuery(Connection conn, String sql, List<Object> params,
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
      long cost = System.nanoTime() / 1000 - startTime;
      NumberFormat format = NumberFormat.getNumberInstance();
      LogUtil.performance().info("executeQuery end. cost: {} μs", format.format(cost));
    }
  }

  static boolean execute(Connection conn, String sql, List<Object> params, ParameterProcess parameterProcess) throws SQLException {
    LogUtil.performance().info("execute begin");
    long startTime = System.nanoTime() / 1000;
    LogUtil.sql().debug("execute: {}", sql);
    try (PreparedStatement statement = conn.prepareStatement(sql)) {
      fillStatementParams(conn, statement, params, parameterProcess);
      return statement.execute();
    } finally {
      long cost = System.nanoTime() / 1000 - startTime;
      NumberFormat format = NumberFormat.getNumberInstance();
      LogUtil.performance().info("execute end. cost: {} μs", format.format(cost));
    }
  }

  static void fillStatementParams(Connection conn,
                                  PreparedStatement statement, List<Object> params,
                                  ParameterProcess parameterProcess) throws SQLException {
    LogUtil.sql().trace("parameter count: {}", params.size());
    for (int index = 0; index < params.size(); index++) {
      Object parameter = params.get(index);
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
