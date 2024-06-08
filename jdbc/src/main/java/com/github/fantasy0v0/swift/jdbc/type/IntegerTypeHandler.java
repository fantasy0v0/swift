package com.github.fantasy0v0.swift.jdbc.type;

import java.sql.*;

public class IntegerTypeHandler extends AbstractTypeHandler<Integer> {

  public IntegerTypeHandler() {
    super(Integer.class, Types.INTEGER);
  }

  @Override
  protected Integer doGetInternal(ResultSet resultSet, int columnIndex) throws SQLException {
    return extract(resultSet, columnIndex, resultSet::getInt);
  }

  @Override
  protected void doSetInternal(Connection con, PreparedStatement ps, int index, Integer parameter) throws SQLException {
    ps.setInt(index, parameter);
  }
}
