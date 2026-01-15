package com.github.fantasy0v0.swift.connection.impl;

import com.github.fantasy0v0.swift.Context;
import com.github.fantasy0v0.swift.connection.ManagedConnection;
import com.github.fantasy0v0.swift.connection.ManagedConnectionPool;

import java.util.HashMap;
import java.util.Map;

public class ManagedConnectionPoolImpl implements ManagedConnectionPool {

  private static final ThreadLocal<Map<Context, ManagedConnection>> threadLocal =
    ThreadLocal.withInitial(HashMap::new);

  @Override
  public ManagedConnection getConnection(Context context) {
    Map<Context, ManagedConnection> map = threadLocal.get();
    if (map.containsKey(context)) {
      ManagedConnection ref = map.get(context);
      return ref.reference();
    }
    ManagedConnection ref = new ManagedConnectionImpl(context.getDataSource(), () -> map.remove(context));
    map.put(context, ref);
    return ref;
  }
}
