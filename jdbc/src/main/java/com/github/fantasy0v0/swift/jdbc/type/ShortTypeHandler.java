package com.github.fantasy0v0.swift.jdbc.type;

import java.sql.*;

public class ShortTypeHandler implements TypeSetHandler<Short> {

  @Override
  public Class<Short> support() {
    return Short.class;
  }

  @Override
  public void doSet(Connection con, PreparedStatement ps, int index, Short parameter) throws SQLException {
    ps.setShort(index, parameter);
  }
}
