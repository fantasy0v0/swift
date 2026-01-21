package com.github.fantasy0v0.swift.connection.impl;

import com.github.fantasy0v0.swift.connection.ManagedConnection;
import com.github.fantasy0v0.swift.connection.ManagedTransaction;
import com.github.fantasy0v0.swift.util.LogUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

class ManagedConnectionImpl implements ManagedConnection {

  private int refCount = 0;

  private Connection connection;

  private final DataSource dataSource;

  private final Runnable onClose;

  ManagedConnectionImpl(DataSource dataSource, Runnable onClose) {
    LogUtil.common().debug("connection create refCount:{}", refCount);
    this.dataSource = dataSource;
    this.onClose = onClose;
  }

  public ManagedConnection retain() {
    refCount += 1;
    LogUtil.common().debug("connection retain refCount:{}", refCount);
    return this;
  }

  @Override
  public ManagedTransaction getTransaction(Integer level) throws SQLException {
    return new ManagedTransactionImpl(this, level);
  }

  @Override
  public void close() throws SQLException {
    if (0 == refCount) {
      this.onClose.run();
      if (null != connection) {
        connection.close();
      }
      LogUtil.common().debug("connection close refCount:{}", refCount);
    } else {
      refCount -= 1;
      LogUtil.common().debug("connection release refCount:{}", refCount);
    }
  }

  @Override
  public Connection unwrap() throws SQLException {
    if (null == connection) {
      connection = dataSource.getConnection();
    }
    return connection;
  }

}
