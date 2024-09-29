package com.github.fantasy0v0.swift.jdbc.spring;

import com.github.fantasy0v0.swift.jdbc.ConnectionReference;
import com.github.fantasy0v0.swift.jdbc.ConnectionTransaction;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

class SpringConnectionReference implements ConnectionReference {

  private final Connection connection;

  private final DataSource dataSource;

  private boolean isClosed = false;

  SpringConnectionReference(Connection connection, DataSource dataSource) {
    this.connection = connection;
    this.dataSource = dataSource;
  }

  @Override
  public ConnectionReference reference() {
    return this;
  }

  @Override
  public ConnectionTransaction getTransaction(Integer level) throws SQLException {
    // 这个连接不会再被使用到, 需要提前释放
    DataSourceUtils.doReleaseConnection(connection, dataSource);
    this.isClosed = true;
    return new SpringConnectionTransaction(level);
  }

  @Override
  public Connection unwrap() {
    return connection;
  }

  @Override
  public void close() throws SQLException {
    if (!isClosed) {
      DataSourceUtils.doReleaseConnection(connection, dataSource);
    }
  }
}
