package com.github.fantasy0v0.swift.jdbc.type;

import java.sql.*;

public class BooleanTypeHandler implements TypeSetHandler<Boolean> {

  @Override
  public Class<Boolean> support() {
    return Boolean.class;
  }

  @Override
  public void doSet(Connection con, PreparedStatement ps, int index, Boolean parameter) throws SQLException {
    ps.setBoolean(index, parameter);
  }
}
