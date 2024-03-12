package com.github.fantasy0v0.swift.jdbc;

import javax.sql.DataSource;
import java.sql.SQLException;

public final class ConnectionPoolUtil {

  static ConnectionPool pool;

  public static ConnectionReference getReference(DataSource dataSource) throws SQLException {
    return pool.getReference(dataSource);
  }

  public static void closeReference(ConnectionReference reference, DataSource dataSource) throws SQLException {
    pool.closeReference(reference, dataSource);
  }

}
