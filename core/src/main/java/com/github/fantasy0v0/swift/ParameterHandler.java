package com.github.fantasy0v0.swift;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface ParameterHandler {

  /**
   * 预编译参数处理
   *
   * @param conn      conn
   * @param statement statement
   * @param index     索引, 与JDBC一致, 从1开始
   * @param parameter 参数
   * @return 是否被处理, 如果自行设置了参数, 需要返回true, 否则返回false使用默认处理方法
   */
  boolean handle(Connection conn,
                 PreparedStatement statement, int index,
                 Object parameter) throws SQLException;

}
