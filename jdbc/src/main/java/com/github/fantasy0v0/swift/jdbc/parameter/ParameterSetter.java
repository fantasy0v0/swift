package com.github.fantasy0v0.swift.jdbc.parameter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

/**
 * 自定义参数的设置方式
 *
 * @author fan 2025/9/27
 */
public interface ParameterSetter<T> {

  Set<Class<T>> support();

  void set(Connection connection, T parameter, PreparedStatement statement, int columnIndex) throws SQLException;

}
