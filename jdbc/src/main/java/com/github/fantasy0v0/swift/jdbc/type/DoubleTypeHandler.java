package com.github.fantasy0v0.swift.jdbc.type;

import java.sql.*;

public class DoubleTypeHandler implements TypeSetHandler<Double> {

  @Override
  public Class<Double> support() {
    return Double.class;
  }

  @Override
  public void doSet(Connection con, PreparedStatement ps, int index, Double parameter) throws SQLException {
    ps.setDouble(index, parameter);
  }
}
