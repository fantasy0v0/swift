package com.github.fantasy0v0.swift.jdbc.type;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class TypeHandler<T> {

  protected final int typeCode;

  protected TypeHandler(int typeCode) {
    this.typeCode = typeCode;
  }

  protected abstract T doGet(ResultSet resultSet, int columnIndex) throws SQLException;

  protected T doGet(ResultSet resultSet, String columnLabel) throws SQLException {
    return doGet(resultSet, resultSet.findColumn(columnLabel));
  }

  protected abstract void doSet(Connection con, PreparedStatement ps, int index, T parameter) throws SQLException;

  public void setParameter(Connection con, PreparedStatement ps, int index, T parameter) throws SQLException {
    if (null == parameter) {
      ps.setNull(index, typeCode);
    } else {
      doSet(con, ps, index, parameter);
    }
  }

  protected <P> P extract(ResultSet resultSet, FunctionWithException1<P> function) throws SQLException {
    P value = function.apply(resultSet);
    return resultSet.wasNull() ? null : value;
  }

  public T getResult(ResultSet resultSet, int columnIndex) throws SQLException {
    return doGet(resultSet, columnIndex);
  }

  public T getResult(ResultSet resultSet, String columnLabel) throws SQLException {
    return doGet(resultSet, columnLabel);
  }

}

interface FunctionWithException1<P> {

  P apply(ResultSet rs) throws SQLException;

}

