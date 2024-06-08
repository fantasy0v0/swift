package com.github.fantasy0v0.swift.jdbc.type;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AbstractTypeHandler<T> implements TypeGetHandler<T>, TypeSetHandler<T> {

  protected final Class<T> typeClass;

  protected final int typeCode;

  protected AbstractTypeHandler(Class<T> typeClass, int typeCode) {
    this.typeClass = typeClass;
    this.typeCode = typeCode;
  }

  protected abstract T doGetInternal(ResultSet resultSet, int columnIndex) throws SQLException;

  protected abstract void doSetInternal(Connection con, PreparedStatement ps, int index, T parameter) throws SQLException;

  @Override
  public Class<T> support() {
    return typeClass;
  }

  @Override
  public T doGet(ResultSet resultSet, int columnIndex) throws SQLException {
    return doGetInternal(resultSet, columnIndex);
  }

  @Override
  public void doSet(Connection con, PreparedStatement ps, int index, T parameter) throws SQLException {
    if (null == parameter) {
      ps.setNull(index, typeCode);
    } else {
      doSetInternal(con, ps, index, parameter);
    }
  }

  protected <P> P extract(ResultSet resultSet, int columnIndex, FunctionWithException1<P> function) throws SQLException {
    P value = function.apply(columnIndex);
    return resultSet.wasNull() ? null : value;
  }

}

interface FunctionWithException1<P> {

  P apply(int columnIndex) throws SQLException;

}

