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
    }
  }

  static boolean execute(Connection conn, String sql, Object[] params, ParameterProcess parameterProcess) throws SQLException {
    try (PreparedStatement statement = conn.prepareStatement(sql)) {
      fillStatementParams(conn, statement, params, parameterProcess);
      return statement.execute();
    }
  }

  static void fillStatementParams(Connection conn,
                                  PreparedStatement statement, Object[] params,
                                  ParameterProcess parameterProcess) throws SQLException {
    for (int index = 0; index < params.length; index++) {
      Object parameter = params[index];
      boolean result = false;
      if (null != parameterProcess) {
        result = parameterProcess.process(conn, statement, index, parameter);
      }
      // 使用默认的处理方法
      if (!result) {
        // TODO
      }
    }
  }

}
