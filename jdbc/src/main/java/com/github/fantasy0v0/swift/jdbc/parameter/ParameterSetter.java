package com.github.fantasy0v0.swift.jdbc.parameter;

import java.sql.Connection;

/**
 * 自定义参数的设置方式
 *
 * @author fan 2025/9/27
 */
public interface ParameterSetter<T> {

  ParameterValue set(Connection connection, T parameter);

}
