package com.github.fantasy0v0.swift.jdbc.parameter;

import com.github.fantasy0v0.swift.jdbc.ResultSetExtractFunction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

/**
 * @author fan 2025/9/29
 */
public abstract class AbstractParameterHandler<T> implements ParameterGetter<T>, ParameterSetter<T> {

  protected final Map<Class<T>, Integer> parameterMap;

  protected AbstractParameterHandler(Class<T> parameterClass, int sqlType) {
    this.parameterMap = Map.of(parameterClass, sqlType);
  }

  protected AbstractParameterHandler(Map<Class<T>, Integer> parameterMap) {
    this.parameterMap = parameterMap;
  }

  /**
   * @return 返回可处理的参数类型
   */
  @Override
  public Set<Class<T>> support() {
    return parameterMap.keySet();
  }

  protected <P> P extract(ResultSet resultSet, int columnIndex,
                          ResultSetExtractFunction<P> function) throws SQLException {
    P value = function.apply(columnIndex);
    return resultSet.wasNull() ? null : value;
  }
}
