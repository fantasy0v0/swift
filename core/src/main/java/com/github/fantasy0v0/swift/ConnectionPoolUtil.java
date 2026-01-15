package com.github.fantasy0v0.swift;

import com.github.fantasy0v0.swift.connection.ManagedConnection;
import com.github.fantasy0v0.swift.connection.ManagedConnectionPool;

import java.sql.SQLException;

public final class ConnectionPoolUtil {

  static ManagedConnectionPool pool;

  public static ManagedConnection getConnection(Context context) throws SQLException {
    return pool.getConnection(context);
  }

}
