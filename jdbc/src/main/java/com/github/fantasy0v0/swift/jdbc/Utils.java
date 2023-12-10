package com.github.fantasy0v0.swift.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class Utils {

  static Connection getConnection(DataSource dataSource) throws SQLException {
    return dataSource.getConnection();
  }

  static PreparedStatement prepareStatement(String sql, Object[] params) {
    return null;
  }

}
