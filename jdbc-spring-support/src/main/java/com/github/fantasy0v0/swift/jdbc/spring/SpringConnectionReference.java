package com.github.fantasy0v0.swift.jdbc.spring;

import com.github.fantasy0v0.swift.jdbc.ConnectionReference;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

class SpringConnectionReference implements ConnectionReference {

  private final Connection connection;

  private final DataSource dataSource;

  SpringConnectionReference(Connection connection, DataSource dataSource) {
    this.connection = connection;
    this.dataSource = dataSource;
  }

  @Override
  public ConnectionReference reference() {
    return this;
  }

  @Override
  public boolean isInner() {
    return DataSourceUtils.isConnectionTransactional(connection, dataSource);
  }

  @Override
  public Connection unwrap() {
    return connection;
  }

  @Override
  public void close() throws SQLException {
    DataSourceUtils.doCloseConnection(connection, dataSource);
  }
}
