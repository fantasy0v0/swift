package com.github.fantasy0v0.swift.jdbc.type;

import java.sql.*;

public class ShortTypeHandler extends AbstractTypeHandler<Short> {

  public ShortTypeHandler() {
    super(Short.class, Types.SMALLINT);
  }

  @Override
  protected Short doGetInternal(ResultSet resultSet, int columnIndex) throws SQLException {
    return extract(resultSet, columnIndex, resultSet::getShort);
  }

  @Override
  protected void doSetInternal(Connection con, PreparedStatement ps, int index, Short parameter) throws SQLException {
    ps.setShort(index, parameter);
  }
}
