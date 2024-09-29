package com.github.fantasy0v0.swift.jdbc.connection;

import com.github.fantasy0v0.swift.jdbc.ConnectionReference;
import com.github.fantasy0v0.swift.jdbc.ConnectionTransaction;
import com.github.fantasy0v0.swift.jdbc.util.LogUtil;

import java.sql.Connection;
import java.sql.SQLException;

class DefaultConnectionReference implements ConnectionReference {

  private int referenceCounting = 0;

  private final Connection connection;

  private final ThreadLocal<ConnectionReference> threadLocal;

  DefaultConnectionReference(Connection connection, ThreadLocal<ConnectionReference> threadLocal) {
    LogUtil.common().debug("connection create rc:{}", referenceCounting);
    this.connection = connection;
    this.threadLocal = threadLocal;
    this.threadLocal.set(this);
  }

  public ConnectionReference reference() {
    referenceCounting += 1;
    LogUtil.common().debug("connection reference rc:{}", referenceCounting);
    return this;
  }

  @Override
  public ConnectionTransaction getTransaction(Integer level) throws SQLException {
    return new DefaultConnectionTransaction(this, level);
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

  @Override
  public Connection unwrap() {
    return connection;
  }

}
