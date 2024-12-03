package com.github.fantasy0v0.swift.jdbc.spring;

import com.github.fantasy0v0.swift.jdbc.Context;
import com.github.fantasy0v0.swift.jdbc.connection.ConnectionReference;
import com.github.fantasy0v0.swift.jdbc.connection.ConnectionTransaction;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
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
