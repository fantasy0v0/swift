package com.github.fantasy0v0.swift.jdbc.parameter;

import java.sql.SQLException;
import java.util.Set;

/**
 * 自定义参数的获取方式
 *
 * @author fan 2025/9/27
 */
public interface ParameterGetter<T> {

  Set<Class<T>> support();

  T get(ParameterMetaData metaData, Object parameter) throws SQLException;

}
