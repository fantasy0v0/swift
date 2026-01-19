package com.github.fantasy0v0.swift.spring;

import com.github.fantasy0v0.swift.Context;
import com.github.fantasy0v0.swift.connection.ManagedConnection;
import com.github.fantasy0v0.swift.connection.ManagedTransaction;
import org.springframework.jdbc.datasource.DataSourceUtils;

import java.sql.Connection;
import java.sql.SQLException;

class ManagedConnectionImpl implements ManagedConnection {

  private Connection connection;

  private final Context context;

  ManagedConnectionImpl(Context context) {
    this.context = context;
  }

  @Override
  public ManagedConnection reference() {
    return this;
  }

  @Override
  public ManagedTransaction getTransaction(Integer level) {
    return new ManagedTransactionImpl(level);
  }

  @Override
  public Connection unwrap() throws SQLException {
    if (null == connection) {
      connection = DataSourceUtils.doGetConnection(context.getDataSource());
    }
    return connection;
  }

  @Override
  public void close() throws SQLException {
    if (null != connection) {
      DataSourceUtils.doReleaseConnection(connection, context.getDataSource());
      connection = null;
    }
  }
}
