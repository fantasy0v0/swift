package com.github.fantasy0v0.swift.spring;

import com.github.fantasy0v0.swift.Context;
import com.github.fantasy0v0.swift.connection.ManagedConnection;
import com.github.fantasy0v0.swift.connection.ManagedConnectionPool;

public class ManagedConnectionPoolImpl implements ManagedConnectionPool {

  @Override
  public ManagedConnection getConnection(Context context) {
    return new ManagedConnectionImpl(context);
  }
}
