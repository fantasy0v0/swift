package com.github.fantasy0v0.swift.jdbc.typehandles;

import com.github.fantasy0v0.swift.jdbc.TypeHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DoubleTypeHandler implements TypeHandler<Double> {

  @Override
  public Class<Double> supported() {
    return Double.class;
  }

  @Override
  public boolean handle(Connection conn, PreparedStatement statement, int index, Double parameter) throws SQLException {
    statement.setDouble(index, parameter);
    return true;
  }
}
