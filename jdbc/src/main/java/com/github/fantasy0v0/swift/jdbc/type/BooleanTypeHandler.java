package com.github.fantasy0v0.swift.jdbc.type;

import java.sql.*;

public class BooleanTypeHandler extends AbstractTypeHandler<Boolean> {

  public BooleanTypeHandler() {
    super(Boolean.class, Types.BOOLEAN);
  }

  @Override
  protected Boolean doGetInternal(ResultSet resultSet, int columnIndex) throws SQLException {
    return extract(resultSet, columnIndex, resultSet::getBoolean);
  }

  @Override
  protected void doSetInternal(Connection con, PreparedStatement ps, int index, Boolean parameter) throws SQLException {
    ps.setBoolean(index, parameter);
  }
}
