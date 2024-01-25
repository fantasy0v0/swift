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
    LogUtil.common().debug("connection create rc:{}", referenceCounting);
    this.connection = connection;
    threadLocal.set(this);
  }

  public ConnectionReference reference() {
    referenceCounting += 1;
    LogUtil.common().debug("connection reference rc:{}", referenceCounting);
    return this;
  }

  @Override
  public void close() throws SQLException {
    if (0 == referenceCounting) {
      threadLocal.remove();
      connection.close();
      LogUtil.common().debug("connection clear rc:{}", referenceCounting);
    } else {
      referenceCounting -= 1;
      LogUtil.common().debug("connection close rc:{}", referenceCounting);
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
