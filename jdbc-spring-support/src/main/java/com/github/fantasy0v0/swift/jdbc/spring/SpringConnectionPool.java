package com.github.fantasy0v0.swift.jdbc.spring;

import com.github.fantasy0v0.swift.jdbc.Context;
import com.github.fantasy0v0.swift.jdbc.connection.ConnectionPool;
import com.github.fantasy0v0.swift.jdbc.connection.ConnectionReference;

import javax.sql.DataSource;
import java.sql.SQLException;

public class SpringConnectionPool implements ConnectionPool {

  @Override
  public ConnectionReference getReference(Context context) {
    return new SpringConnectionReference(context);
  }
}
