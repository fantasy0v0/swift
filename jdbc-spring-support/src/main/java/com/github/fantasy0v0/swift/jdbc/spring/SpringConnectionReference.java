package com.github.fantasy0v0.swift.jdbc.spring;

import com.github.fantasy0v0.swift.jdbc.ConnectionReference;
import com.github.fantasy0v0.swift.jdbc.ConnectionTransaction;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

class SpringConnectionReference implements ConnectionReference {

  private Connection connection;

  private final DataSource dataSource;

  SpringConnectionReference(DataSource dataSource) {
    this.dataSource = dataSource;
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
      connection = DataSourceUtils.doGetConnection(dataSource);
    }
    return connection;
  }

  @Override
  public void close() throws SQLException {
    if (null != connection) {
      DataSourceUtils.doReleaseConnection(connection, dataSource);
    }
  }
}
