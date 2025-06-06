package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.connection.ConnectionPool;
import com.github.fantasy0v0.swift.jdbc.connection.ConnectionReference;

import javax.sql.DataSource;
import java.sql.SQLException;

public final class ConnectionPoolUtil {

  static ConnectionPool pool;

  public static ConnectionReference getReference(Context context) throws SQLException {
    return pool.getReference(context);
  }

}
