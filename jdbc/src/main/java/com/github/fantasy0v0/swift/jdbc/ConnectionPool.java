package com.github.fantasy0v0.swift.jdbc;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * 数据库连接池
 */
public interface ConnectionPool {

  ConnectionReference getReference(DataSource dataSource) throws SQLException;

  void closeReference(ConnectionReference reference, DataSource dataSource) throws SQLException;

}
