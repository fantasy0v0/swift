package com.github.fantasy0v0.swift.jdbc.type;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface TypeGetHandler<T> {

  Class<T> support();

  T doGet(ResultSet resultSet, int columnIndex) throws SQLException;

  default T doGet(ResultSet resultSet, String columnLabel) throws SQLException {
    return doGet(resultSet, resultSet.findColumn(columnLabel));
  }

}
