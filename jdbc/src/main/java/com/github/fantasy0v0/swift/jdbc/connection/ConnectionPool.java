package com.github.fantasy0v0.swift.jdbc.connection;

import com.github.fantasy0v0.swift.jdbc.Context;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * 数据库连接池
 */
public interface ConnectionPool {

  ConnectionReference getReference(Context context) throws SQLException;

}
