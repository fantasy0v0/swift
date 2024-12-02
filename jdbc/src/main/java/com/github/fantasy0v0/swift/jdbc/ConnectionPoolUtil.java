package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.connection.ConnectionPool;
import com.github.fantasy0v0.swift.jdbc.connection.ConnectionReference;

import javax.sql.DataSource;
import java.sql.SQLException;

public final class ConnectionPoolUtil {

  static ConnectionPool pool;

  /**
   * TODO 需要改成Context
   */
  public static ConnectionReference getReference(DataSource dataSource) throws SQLException {
    return pool.getReference(dataSource);
  }

  public static void closeReference(ConnectionReference reference, DataSource dataSource) throws SQLException {
    pool.closeReference(reference, dataSource);
  }

}
