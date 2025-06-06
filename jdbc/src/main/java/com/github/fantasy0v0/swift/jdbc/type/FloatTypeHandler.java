package com.github.fantasy0v0.swift.jdbc.type;

import java.sql.*;

public class FloatTypeHandler implements TypeSetHandler<Float> {

  @Override
  public Class<Float> support() {
    return Float.class;
  }

  @Override
  public void doSet(Connection con, PreparedStatement ps, int index, Float parameter) throws SQLException {
    ps.setFloat(index, parameter);
  }
}
