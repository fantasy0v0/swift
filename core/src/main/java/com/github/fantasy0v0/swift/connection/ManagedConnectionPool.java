package com.github.fantasy0v0.swift.connection;

import com.github.fantasy0v0.swift.Context;

import java.sql.SQLException;

/**
 * 数据库连接池
 */
public interface ManagedConnectionPool {

  ManagedConnection getConnection(Context context) throws SQLException;

}
