package com.github.fantasy0v0.swift.jdbc.spring;

import com.github.fantasy0v0.swift.jdbc.ConnectionPool;
import com.github.fantasy0v0.swift.jdbc.ConnectionReference;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class SpringConnectionPool implements ConnectionPool {

  @Override
  public ConnectionReference getReference(DataSource dataSource) {
    Connection connection = DataSourceUtils.getConnection(dataSource);
    return new SpringConnectionReference(connection, dataSource);
  }

  @Override
  public void closeReference(ConnectionReference reference, DataSource dataSource) throws SQLException {
    reference.close();
  }
}
