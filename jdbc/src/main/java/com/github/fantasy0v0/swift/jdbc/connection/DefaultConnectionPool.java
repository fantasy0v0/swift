package com.github.fantasy0v0.swift.jdbc.connection;

import com.github.fantasy0v0.swift.jdbc.ConnectionPool;
import com.github.fantasy0v0.swift.jdbc.ConnectionReference;

import javax.sql.DataSource;
import java.sql.SQLException;

public class DefaultConnectionPool implements ConnectionPool {

  private static final ThreadLocal<ConnectionReference> threadLocal = new ThreadLocal<>();

  @Override
  public ConnectionReference getReference(DataSource dataSource) {
    ConnectionReference ref = threadLocal.get();
    if (null != ref) {
      return ref.reference();
    }
    return new DefaultConnectionReference(dataSource, threadLocal);
  }

  @Override
  public void closeReference(ConnectionReference reference, DataSource dataSource) throws SQLException {
    reference.close();
  }
}
