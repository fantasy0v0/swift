package com.github.fantasy0v0.swift.jdbc.parameter;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Set;

/**
 * 自定义参数的获取方式
 *
 * @author fan 2025/9/27
 */
public interface ParameterGetter<T> {

  /**
   * @return 返回执行的参数类型
   */
  Set<Class<? extends T>> support();

  /**
   * 获取参数
   *
   * @param resultSetMetaData metaData
   * @param columnIndex       columnIndex
   * @param parameter         jdbc返回的参数, 可能为null
   * @return 处理后的指定类型的参数
   * @throws SQLException 处理失败
   */
  T get(ResultSetMetaData resultSetMetaData, int columnIndex, Object parameter) throws SQLException;

}
