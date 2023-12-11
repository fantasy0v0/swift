package com.github.fantasy0v0.swift.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface ParameterProcess {

  /**
   * 预编译参数处理
   * @param conn      conn
   * @param statement statement
   * @param index     索引
   * @param parameter 参数
   * @return 是否被处理, 如果自行设置了参数, 需要返回true, 否则返回false使用默认处理方法
   */
  boolean process(Connection conn,
                  PreparedStatement statement, int index,
                  Object parameter) throws SQLException;
}
