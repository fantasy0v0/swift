package com.github.fantasy0v0.swift.spring;

import com.github.fantasy0v0.swift.Context;
import com.github.fantasy0v0.swift.connection.ConnectionReference;
import com.github.fantasy0v0.swift.connection.ConnectionTransaction;
import org.springframework.jdbc.datasource.DataSourceUtils;

import java.sql.Connection;
import java.sql.SQLException;

class SpringConnectionReference implements ConnectionReference {

  private Connection connection;

  private final Context context;

  SpringConnectionReference(Context context) {
    this.context = context;
  }

  @Override
  public ConnectionReference reference() {
    return this;
  }

  @Override
  public ConnectionTransaction getTransaction(Integer level) {
    return new SpringConnectionTransaction(level);
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
    }
  }
}
