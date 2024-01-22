package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.util.LogUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionReference implements AutoCloseable {

  private static final ThreadLocal<ConnectionReference> threadLocal = new ThreadLocal<>();


  private int referenceCounting = 0;

  private final Connection connection;

  ConnectionReference(Connection connection) {
    LogUtil.common().debug("connection create");
    this.connection = connection;
    threadLocal.set(this);
  }

  public ConnectionReference reference() {
    LogUtil.common().debug("connection reference");
    referenceCounting += 1;
    return this;
  }

  @Override
  public void close() throws SQLException {
    LogUtil.common().debug("connection close");
    if (0 == referenceCounting) {
      threadLocal.remove();
      connection.close();
      LogUtil.common().debug("connection clear");
    } else {
      referenceCounting -= 1;
    }
  }

  boolean isInner() {
    return referenceCounting > 0;
  }

  public Connection unwrap() {
    return connection;
  }

  public static ConnectionReference getReference(DataSource dataSource) throws SQLException {
    ConnectionReference ref = threadLocal.get();
    if (null != ref) {
      return ref.reference();
    }
    return new ConnectionReference(dataSource.getConnection());
  }
}
