package com.github.fantasy0v0.swift;

import com.github.fantasy0v0.swift.connection.ConnectionPool;
import com.github.fantasy0v0.swift.connection.ConnectionReference;

import java.sql.SQLException;

public final class ConnectionPoolUtil {

  static ConnectionPool pool;

  public static ConnectionReference getReference(Context context) throws SQLException {
    return pool.getReference(context);
  }

}
