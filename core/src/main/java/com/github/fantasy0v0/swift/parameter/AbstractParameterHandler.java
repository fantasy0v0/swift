package com.github.fantasy0v0.swift.parameter;

import com.github.fantasy0v0.swift.ResultSetExtractFunction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author fan 2025/9/29
 */
public abstract class AbstractParameterHandler<T> implements ParameterGetter<T>, ParameterSetter<T> {

  protected final Map<Class<? extends T>, Integer> parameterMap = new HashMap<>();

  protected AbstractParameterHandler(Class<T> parameterClass, int sqlType) {
    this.parameterMap.put(parameterClass, sqlType);
  }

  protected AbstractParameterHandler() {

  }

  /**
   * @return 返回可处理的参数类型
   */
  @Override
  public Set<Class<? extends T>> support() {
    return parameterMap.keySet();
  }

  protected <P> P extract(ResultSet resultSet, int columnIndex,
                          ResultSetExtractFunction<P> function) throws SQLException {
    P value = function.apply(columnIndex);
    return resultSet.wasNull() ? null : value;
  }
}
