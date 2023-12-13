package com.github.fantasy0v0.swift.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Row {

  private final ResultSet resultSet;

  Row(ResultSet resultSet) {
    this.resultSet = resultSet;
  }

  public Long getLong(int columnIndex) throws SQLException {
    long value = resultSet.getLong(columnIndex);
    if (resultSet.wasNull()) {
      return null;
    }
    return value;
  }

  public String getString(int columnIndex) throws SQLException {
    String value = resultSet.getString(columnIndex);
    if (resultSet.wasNull()) {
      return null;
    }
    return value;
  }

}
