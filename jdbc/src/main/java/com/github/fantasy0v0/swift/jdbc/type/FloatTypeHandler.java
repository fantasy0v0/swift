package com.github.fantasy0v0.swift.jdbc.type;

import java.sql.*;

public class FloatTypeHandler extends AbstractTypeHandler<Float> {

  public FloatTypeHandler() {
    super(Float.class, Types.FLOAT);
  }

  @Override
  protected Float doGetInternal(ResultSet resultSet, int columnIndex) throws SQLException {
    return extract(resultSet, columnIndex, resultSet::getFloat);
  }

  @Override
  protected void doSetInternal(Connection con, PreparedStatement ps, int index, Float parameter) throws SQLException {
    ps.setFloat(index, parameter);
  }
}
