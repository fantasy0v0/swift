package com.github.fantasy0v0.swift.spring;

import com.github.fantasy0v0.swift.Context;
import com.github.fantasy0v0.swift.connection.ConnectionPool;
import com.github.fantasy0v0.swift.connection.ConnectionReference;

public class SpringConnectionPool implements ConnectionPool {

  @Override
  public ConnectionReference getReference(Context context) {
    return new SpringConnectionReference(context);
  }
}
