package com.github.fantasy0v0.swift.jdbc.connection.impl;

import com.github.fantasy0v0.swift.jdbc.Context;
import com.github.fantasy0v0.swift.jdbc.connection.ConnectionPool;
import com.github.fantasy0v0.swift.jdbc.connection.ConnectionReference;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DefaultConnectionPool implements ConnectionPool {

  private static final ThreadLocal<Map<Context, ConnectionReference>> threadLocal = new ThreadLocal<>();

  static {
    threadLocal.set(new HashMap<>());
  }

  @Override
  public ConnectionReference getReference(Context context) {
    Map<Context, ConnectionReference> map = threadLocal.get();
    if (map.containsKey(context)) {
      ConnectionReference ref = map.get(context);
      return ref.reference();
    }
    ConnectionReference ref = new DefaultConnectionReference(context.getDataSource(), () -> map.remove(context));
    map.put(context, ref);
    return ref;
  }
}
