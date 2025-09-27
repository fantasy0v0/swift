package com.github.fantasy0v0.swift.jdbc.parameter;

/**
 * 自定义参数的获取方式
 *
 * @author fan 2025/9/27
 */
public interface ParameterGetter<T> {

  T get(int columnType, int precision, int scale, Object parameter);

}
