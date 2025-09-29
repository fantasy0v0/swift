package com.github.fantasy0v0.swift.jdbc.parameter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

/**
 * 自定义参数的获取方式
 *
 * @author fan 2025/9/27
 */
public interface ParameterGetter<T> {

  Set<Class<T>> support();

  T get(ResultSet resultSet, int columnIndex) throws SQLException;

  default T get(ResultSet resultSet, String columnLabel) throws SQLException {
    return get(resultSet, resultSet.findColumn(columnLabel));
  }

}
